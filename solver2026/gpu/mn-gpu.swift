// Host for the GPU port of the 1997 m^n Mastermind heuristic.
// Compiles mastermind.metal at runtime, dispatches one thread per game,
// reduces guess counts on the CPU.
//
// Usage: mn-gpu <colors> <positions> <runs> [seed]
import Metal
import Foundation

struct Params {
    var colors: UInt32
    var positions: UInt32
    var runs: UInt32
    var base: UInt32
    var seed: UInt64
}

func log(_ s: String) {
    FileHandle.standardError.write("[mn-gpu] \(s)\n".data(using: .utf8)!)
}

let args = CommandLine.arguments
guard args.count >= 4,
      let colors = UInt32(args[1]), let positions = UInt32(args[2]), let runs = UInt32(args[3])
else {
    FileHandle.standardError.write("usage: mn-gpu <colors> <positions> <runs> [seed]\n".data(using: .utf8)!)
    exit(1)
}
let seed: UInt64 = args.count > 4 ? UInt64(args[4]) ?? 42 : UInt64(Date().timeIntervalSince1970 * 1000)

guard colors <= 24 && positions <= 16 else {
    FileHandle.standardError.write("limits: colors <= 24, positions <= 16\n".data(using: .utf8)!)
    exit(1)
}

let device = MTLCreateSystemDefaultDevice()!
let srcURL = URL(fileURLWithPath: args[0]).deletingLastPathComponent().appendingPathComponent("mastermind.metal")
let source = try String(contentsOf: srcURL, encoding: .utf8)
let library = try device.makeLibrary(source: source, options: nil)
let fn = library.makeFunction(name: "play")!
let pipeline = try device.makeComputePipelineState(function: fn)
let queue = device.makeCommandQueue()!

let resultBuf = device.makeBuffer(length: Int(runs) * MemoryLayout<UInt32>.stride, options: .storageModeShared)!

log("device: \(device.name)  space: \(colors)^\(positions) = \(pow(Double(colors), Double(positions)))  games: \(runs)  seed: \(seed)")
log("pipeline: maxThreadsPerThreadgroup=\(pipeline.maxTotalThreadsPerThreadgroup) threadExecutionWidth=\(pipeline.threadExecutionWidth)")

let t0 = DispatchTime.now()
let tgSize = MTLSize(width: min(pipeline.maxTotalThreadsPerThreadgroup, 64), height: 1, depth: 1)
let chunk: UInt32 = 262_144
var base: UInt32 = 0
while base < runs {
    let n = min(chunk, runs - base)
    var p = Params(colors: colors, positions: positions, runs: runs, base: base, seed: seed)
    let cb = queue.makeCommandBuffer()!
    let enc = cb.makeComputeCommandEncoder()!
    enc.setComputePipelineState(pipeline)
    enc.setBytes(&p, length: MemoryLayout<Params>.stride, index: 0)
    enc.setBuffer(resultBuf, offset: 0, index: 1)
    enc.dispatchThreads(MTLSize(width: Int(n), height: 1, depth: 1), threadsPerThreadgroup: tgSize)
    enc.endEncoding()
    cb.commit()
    cb.waitUntilCompleted()
    if cb.status == .error {
        log("GPU command buffer error: \(String(describing: cb.error))")
        exit(2)
    }
    base += n
    let el = Double(DispatchTime.now().uptimeNanoseconds - t0.uptimeNanoseconds) / 1e9
    let rate = Double(base) / el
    // running stats over completed games
    let res = resultBuf.contents().bindMemory(to: UInt32.self, capacity: Int(base))
    var s = 0.0
    for i in 0..<Int(base) { s += Double(res[i]) }
    log(String(format: "%d/%d games  avg=%.4f  %.2fs elapsed  %.0f games/s  eta %.1fs",
               base, runs, s / Double(base), el, rate, Double(runs - base) / rate))
}

let t1 = DispatchTime.now()
let ms = Double(t1.uptimeNanoseconds - t0.uptimeNanoseconds) / 1e6

let res = resultBuf.contents().bindMemory(to: UInt32.self, capacity: Int(runs))
var sum = 0.0, sumsq = 0.0
var maxG: UInt32 = 0
var failed = 0
var hist = [Int](repeating: 0, count: 64)
for i in 0..<Int(runs) {
    let g = res[i]
    if g == 0xFFFF || g == 0 { failed += 1; continue }
    sum += Double(g); sumsq += Double(g) * Double(g)
    if g > maxG { maxG = g }
    hist[min(Int(g), 63)] += 1
}
log("histogram: " + (1...max(Int(maxG), 1)).compactMap { hist[$0] > 0 ? "\($0):\(hist[$0])" : nil }.joined(separator: " "))
let n = Double(Int(runs) - failed)
let avg = sum / n
let sd = ((sumsq / n - avg * avg) / n).squareRoot()
print(String(format: "Colors: %-3d Positions: %-3d Runs: %-8d Average: %-8.4f Std.Dev: %-8.5f Maximum: %-3d Time: %.1f ms%@",
             colors, positions, runs, avg, sd, maxG, ms,
             failed > 0 ? "  (failed: \(failed))" : ""))
