ORIGIN		= Symantec Café
ORIGIN_VER	= Version 7.22
VERSION		= DEBUG

!IFDEF SUB_DEBUG
DEBUG		= $(SUB_DEBUG)
NDEBUG		= !$(SUB_DEBUG)
!ELSE
DEBUG		= 1
NDEBUG		= 0
!ENDIF

PROJ		= tester
APPTYPE		= JAVA CONSOLE
PROJTYPE	= class

JAVAC		= sj
MAKE		= SMAKE
LNK		= LINK


HEADERS		= 
!IF $(DEBUG)
OUTPUTDIR	= .
CREATEOUTPUTDIR	=
TARGETDIR	= .
CREATETARGETDIR	=

CFLAGS		=  -g 
DEFINES		= 
!ELSE
OUTPUTDIR	= .
CREATEOUTPUTDIR	=
TARGETDIR	= .
CREATETARGETDIR	=

CFLAGS		=  -O 
DEFINES		= 
!ENDIF

MFLAGS		= MASTERPROJ=$(PROJ) 
DEBUGGERFLAGS	=  
PAR		= PROJS BATS SRCS

INCLUDES	= 



SRCS		= mmsTester.java RandomIntGenerator.java mmsTester2.java 

BATS		= 

.SUFFIXES: .JAVA .CLASS 

.JAVA.CLASS:
	$(JAVAC) $(CFLAGS) $(INCLUDES) $*.java



all:	noteout createdir $(PRECOMPILE) compall $(POSTCOMPILE) _done


all2:	createdir $(PRECOMPILE) compall $(POSTCOMPILE) _done


all3:	createdir $(PRECOMPILE) compall2 $(POSTCOMPILE) _done


compall:	$(OBJS)
	$(JAVAC) $(CFLAGS) $(INCLUDES) -make -cdb $(PROJ).cdb @<<
	$(SRCS)
<<


compall2:	$(OBJS)
	$(JAVAC) $(CFLAGS) $(INCLUDES) -cdb $(PROJ).cdb @<<
	$(SRCS)
<<

noteout:
	REM Output to $(OUTPUTDIR)

createdir:
	$(CREATEOUTPUTDIR)
	$(CREATETARGETDIR)
	
_done:
		REM  Project is up to date

buildall:	clean	all3


clean:
		-del $(TARGETDIR)\$$SCW$$.$(PROJTYPE)
		-del $(TARGETDIR)\$(PROJ).CLE
		-del tester.dpd
		-del $(OBJS)




