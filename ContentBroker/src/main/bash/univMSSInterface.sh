#!/bin/sh

# This script is a template which must be updated if one wants to use the universal MSS driver.
# Your working version should be in this directory server/bin/cmd/univMSSInterface.sh.
# Functions to modify: syncToArch, stageToCache, mkdir, chmod, rm, stat
# These functions need one or two input parameters which should be named $1 and $2.
# If some of these functions are not implemented for your MSS, just let this function as it is.
#

# Anpassungen fuer DNSCORE  TSM: Jens Peters

LOG="../log/mss_`date +%m-%Y`.log"
DSMBIN=     
DSM_CONFIG=
# function for the synchronization of file $1 on local disk resource to file $2 in the MSS
syncToArch () {
	echo "`date` syncToArch File $1" >>$LOG
	# <your command or script to copy from cache to MSS> $1 $2   
    $DSMBIN archive $1 -optfile=$DSM_CONFIG >>/dev/null
    error=$?
	if [ $error != 0 ] # if file does not exist or information not available
	then
		echo "`date` archiving on tsm for $1 failed!" >>$LOG
		return $error
	fi
	echo "`date` syncToArch of $1 successful" >>$LOG
	touch $2
	return 0
}

# function for staging a file $1 from the MSS to file $2 on disk
# wenn er nicht auf der Cache Ress ist!
stageToCache () {
	# <your command to stage from MSS to cache> $1 $2	
	echo "`date` stageToCache File $1 to $2" >>$LOG
    ret=`echo "$1" | sed 's/tsm/tsmcache/'`
	echo "`date` stageToCache File from TSM with path $ret" >>$LOG
	$DSMBIN retrieve "$ret" -Replace=yes -optfile=$DSM_CONFIG $2 >>/dev/null
	error=$?
	if [ $error != 0 ] # if file does not exist or information not available
	then
		echo "`date` staging back to filesystem of file $ret failed ERR: $error!" >>$LOG
      		if [ $error == 8 ]
        	then
             	echo "`date` setting dsmc error $error to 0!" >>$LOG
            	return 0
        	fi
		return $error
	fi
	echo "`date` staging back to filesystem of file $ret successful" >>$LOG	
	return 0
}

# function to create a new directory $1 in the MSS logical name space
mkdir () {
	# <your command to make a directory in the MSS> $1
	# e.g.: /usr/local/bin/rfmkdir -p rfioServerFoo:$1
	echo "`date` mkdir $1" >>$LOG
	/bin/mkdir -p $1
	return 0
}

# function to modify ACLs $2 (octal) in the MSS logical name space for a given directory $1 
chmod () {
	echo "`date` chmod on TSM directory $1 to $2 not implemeted yet!" >>$LOG
	# <your command to modify ACL> $1 $2
	# e.g: /usr/local/bin/rfchmod $2 rfioServerFoo:$1
	return 0
}

# function to remove a file $1 from the MSS
rm () {
	# <your command to remove a file from the MSS> $1
	# e.g: /usr/local/bin/rfrm rfioServerFoo:$1
	# jp: die Pakete liegen auf tsm im Cache ordner, daher muessen
	# wir hier den virtuellen Pfad auf tsmcache aendern. 

	del=`echo "$1" | sed 's/tsm/tsmcache/'`
	echo "`date` to be deleted on tsm archive: $del" >>$LOG
	#$DSMBIN delete archive $del -noprompt -subdir=no -optfile=$DSM_CONFIG
	error=$?
	if [ $error == 0 ]
	then
		echo "`date` $del ...deleted" >>$LOG
		/bin/rm -f $1;
		return 0
	fi
	return $error
}

# function to do a stat on a file $1 stored in the MSS
stat () {
	#schoen, wenn das vom TSM kaeme
		
	# %a   Zugriffsrechte im Oktalformat
	#  %A   Zugriffsrechte in menschenlesbarer Form
	#  %b   Anzahl der beanspruchten Blöcke (siehe %B)
	#  %B   die Größe in Bytes jedes mit „%b“ gemeldeten Blocks
	#  %C   SELinux-Sicherheitskontext-Zeichenkette
	#  %d   Gerätenummer in Dezimal
	#  %D   Gerätenummer in Hex
	#  %f   roher Modus in Hex
	#  %F   Dateityp
	#  %g   Gruppen‐ID des Eigners
	#  %G   Gruppenname des Eigners
	#  %i   INode‐Nummer
	#  %h   Anzahl der harten Verknüpfungen
	#  %n   Dateiname
	#  %N   „Quoted File Name“ mit Dereferenzierung bei symbolischer Verknüpfung
	#  %o   E/A‐Blockgröße
	#  %s   Gesamtgröße in Bytes
	#  %t   Major‐Gerätetyp in Hex
	#  %T   Minor‐Gerätetyp in Hex
	#  %u   Nutzer‐ID des Eigners
	#  %U   Nutzername des Eigners
	#  %x   Zeit des letzten Zugriffs
	#  %X   Zeit des letzten Zugriffs in Sekunden seit der Epoche
	#  %y   Zeit der letzten Modifikation
	#  %Y   Zeit der letzten Modifikation in Sekunden seit der Epoche
	#  %z   Zeit der letzten Änderung
	#  %Z   Zeit der letzten Änderung in Sekunden seit der Epoche
		
	
	echo "`date` stat File/Dir $1" >>$LOG
	
	#echo "$device:$inode:$mode:$nlink:$uid:$gid:$devid:$size:$blksize:$blkcnt:$atime:$mtime:$ctime"
	
	output=`/usr/bin/stat -c%d:%i:%a:%h:%u:%g:%D:%s:%o:%b: $1`

	error=$?
	if [ $error != 0 ] # if file does not exist or information not available
	then
		echo "`date` File/Dir not found $1 $error" >>$LOG	
		return $error
	fi
	
	#device=`echo $output | awk '{print $1}'`	
	#inode=`echo $output | awk '{print $2}'`	
	#mode=`echo $output | awk '{print $3}'`
	#nlink=`echo $output | awk '{print $4}'`
	#uid=`echo $output | awk '{print $5}'`
	#gid=`echo $output | awk '{print $6}'`
	#devid=`echo $output | awk '{print $7}'`
	#size=`echo $output | awk '{print $8}'`
	#blksize=`echo $output | awk '{print $9}'`
	#blkcnt=`echo $output | awk '{print $10}'`
	
	atime=date --utc `/usr/bin/stat -c%x`--date  +"%Y-%m-%d-%H:%M:%S"
	mtime=date --utc `/usr/bin/stat -c%y`--date  +"%Y-%m-%d-%H:%M:%S"
	ctime=date --utc `/usr/bin/stat -c%z`--date  +"%Y-%m-%d-%H:%M:%S"
	
	echo "$output:$atime:$mtime:$ctime"
	echo "`date` stat File $output:$atime:$mtime:$ctime" >>$LOG
	
	#device=`echo $output | awk '{print $7}'`	
	#inode=`echo $output | awk '{print $8}'`	
	#mode=`echo $output | awk '{print $8}'`
	#jptest2.ir 71 8 81b4 505 505 fd02 10225179 1 0 0 1306511580 1306511571 1306511571 4096
	
	#File: „jptest2.ir“
    #Size: 71        	Blocks: 8          IO Block: 4096   reguläre Datei
	#Device: fd02h/64770d	Inode: 10225179    Links: 1
	#Access: (0664/-rw-rw-r--)  Uid: (  505/   irods)   Gid: (  505/   irods)
	#Access: 2011-05-27 17:53:00.504675105 +0200
	#Modify: 2011-05-27 17:52:51.370777993 +0200
	#Change: 2011-05-27 17:52:51.370777993 +0200
	
	
	
	#sel=`echo "$1" | sed 's/tsm/tsmcache/'`
	#$DSMBIN query archive $sel -optfile=$DSM_CONFIG
	# hat einen
	#Returncode von 0 wenn eine Datei gefunden wird, sonst 8.
	

	
	# parse the output.
	# Parameters to retrieve: device ID of device containing file("device"), 
	#                         file serial number ("inode"), ACL mode in octal ("mode"),
	#                         number of hard links to the file ("nlink"),
	#                         user id of file ("uid"), group id of file ("gid"),
	#                         device id ("devid"), file size ("size"), last access time ("atime"),
	#                         last modification time ("mtime"), last change time ("ctime"),
	#                         block size in bytes ("blksize"), number of blocks ("blkcnt")
	# e.g: device=`echo $output | awk '{print $3}'`	
	# Note 1: if some of these parameters are not relevant, set them to 0.
	# Note 2: the time should have this format: YYYY-MM-dd-hh.mm.ss with: 
	#                                           YYYY = 1900 to 2xxxx, MM = 1 to 12, dd = 1 to 31,
	#                                           hh = 0 to 24, mm = 0 to 59, ss = 0 to 59
	return 
}

#############################################
# below this line, nothing should be changed.
#############################################

case "$1" in
	syncToArch ) $1 $2 $3 ;;
	stageToCache ) $1 $2 $3 ;;
	mkdir ) $1 $2 ;;
	chmod ) $1 $2 $3 ;;
	rm ) $1 $2 ;;
	stat ) $1 $2 ;;
esac

exit $?
