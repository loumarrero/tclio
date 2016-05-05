# This file contains bundled tcl procedures. These procedures can be called
# within scripts.


##################################################################################
# This procedure checks if given testVersion is in the given supportedVersionRange
# This procedure will be used to check device software version against ATL range of supported
# devices. For example, to test if a device is UPM cabable:
# Step 1: Get the UPM ATL attribute value
# set var upmatlvalue [GetATLValue UPM]
# Step 2: use this procedure to test if this device supports UPM
# set result [isVersionSupported $upmatlvalue $deviceSoftwareVer]
#
# return : 1 if supported, 0 otherwise
#######################################################################################
proc isVersionSupported {supportedVersionRange testVersion } {
  package require java
  set jVersionNumberRangeSet [java::new com.extremenetworks.everest.common.util.VersionNumberRangeSet]
  set jVersion [java::new com.extremenetworks.everest.common.util.VersionNumber $testVersion]
  set jParseResult [$jVersionNumberRangeSet parse $supportedVersionRange]
  set result [$jVersionNumberRangeSet contains $jVersion]
  return $result
}

##################################################################################
# This procedure zips contents of given folder and put zip file
# in the parent directory with the name <folder>.zip
# Example usage:
# set var zipResult [zipFolder "$fullFilePath"]
#         if {$zipResult == 1} {
#		  }
#
# return : 1 if success, 0 otherwise
#######################################################################################
proc zipFolder {folderPath } {
  package require java
  set jFolder [java::new java.io.File $folderPath]
  set jParentFolder [$jFolder getParent]
  set jNull [java::null]
  set result [java::call com.extremenetworks.everest.common.util.ZipFileUtil makeZipFile $jFolder $jParentFolder $jNull]
  return $result
}

proc isConfigFileFormatXML { filePath } {
  package require java
  set result [java::call com.extremenetworks.everest.common.util.CMFileUtility isConfigFileFormatXML $filePath]
  return $result
}

##################################################################################
# This procedure unzips fiven zip file
# return : list of all file names in the zip file
#######################################################################################
proc unzipFolder {folderPath } {
    package require java
  set result [java::call com.extremenetworks.everest.common.util.ZipFileUtil unZip $folderPath]
  set size [$result size]
  set i 0

  while {$i < $size} {
  set fileName [$result get $i]
  lappend fileNames [$fileName toString]
  incr i
  }
  return $fileNames
}

##################################################################################
# Returns TFTP root on the NetSight server.
# return : string path of tftp root base directory
#######################################################################################

proc getTftpRoot {} {
 package require java
 set tftproot [java::call com.extremenetworks.everest.common.data.FileResources getTftpRoot]
 return $tftpRoot
}
proc printAllVariables {} {

}

##################################################################################
# Checks if given atl property is supported on the switch.
# property: atl property name
# abortOnError: script abort_on_error property, values: true or false
# softwareVersion: switch software version. Pass $deviceSoftwareVer system variable
# returns true / false
##################################################################################
proc isATLPropertySupported {property} {
global deviceSoftwareVer
global abort_on_error

if {[info exists abort_on_error]} {
 set saveAbort_on_error $abort_on_error
}
# set abort_on_error to 0 to ignore errors in this procedure
set abort_on_error 0
set result true
set atlValue [GetATLValue $property]

if {$STATUS == 0} {
   if {[string equal -nocase $atlValue "TRUE"]} {
    return $result
   }
   set atlPropertySupported [isVersionSupported $atlValue $deviceSoftwareVer]
   if {!$atlPropertySupported} {
     set result false
   }
 } else {
   set result false
 }

 if {[info exists abort_on_error]} {
  # reset abort_on_error sys variable back to what it was when proc entered
  set abort_on_error $saveAbort_on_error
 }

 return $result
}

proc printSystemVariables {} {
  global deviceIP
  global deviceName
  global deviceSoftwareVer
  global serverName
  global serverIP
  global serverPort
  global date
  global time
  global abort_on_error
  global CLI.OUT
 # global runMode
  global deviceType
  global netsightUser
  global CLI.SESSION_TYPE
  global isExos
  global vendor

#    if {![info exists runMode]} {
#     set runMode EPICENTER
#    }

    if {[info exists deviceName]} {
    ECHO "$deviceName #printSystemVariables"
  }
    ECHO "System variable properties:"
    ECHO "-------------------------------"
    if {[info exists deviceName]} {
    ECHO "deviceName: $deviceName"
  }

    if {[info exists deviceIP]} {
   ECHO "deviceIP: $deviceIP"
  }

  if {[info exists deviceType]} {
    ECHO "deviceType: $deviceType"
  }

  if {[info exists vendor]} {
    ECHO "vendor: $vendor"
  }

  if {[info exists isExos]} {
    ECHO "isExos: $isExos"
  }

  if {[info exists serverName]} {
    ECHO "serverName: $serverName"
  }

  if {[info exists deviceSoftwareVer]} {
    ECHO "deviceSoftwareVer: $deviceSoftwareVer"
  }

  if {[info exists serverIP]} {
    ECHO "serverIP: $serverIP"
  }

  if {[info exists serverPort]} {
    ECHO "serverPort: $serverPort"
  }

  if {[info exists date]} {
    ECHO "date: $date"
  }

  if {[info exists time]} {
    ECHO "time: $time"
  }

  if {[info exists abort_on_error]} {
    ECHO "abort_on_error: $abort_on_error"
  }

  if {[info exists {CLI.OUT}]} {
    ECHO "CLI.OUT: ${CLI.OUT}"
  }

  #if {[info exists runMode]} {
  #  ECHO "runMode: $runMode"
  #}

  if {[info exists {CLI.SESSION_TYPE}]} {
    ECHO "CLI.SESSION_TYPE: ${CLI.SESSION_TYPE}"
  }
  if {[info exists netsightUser]} {
    ECHO "netsightUser: $netsightUser"
  }
  ECHO ""
}

proc printHeader {} {
  global deviceIP
  global deviceName
  global date
  global time
  global netsightUser
  ECHO "User: $netsightUser"
  ECHO "$deviceName    $deviceIP    $date at $time"
}

# sleeps for N seconds
proc sleep {N} {
  global deviceName
  ECHO "$deviceName #sleep $N"
    after [expr {int($N * 1000)}]
}


# parse show vlan command to return VR name for the device
proc getDeviceVRName {} {
    global deviceIP
    global isExos
  package require java
  set jTclJavaUtils [java::new com.extremenetworks.epicenter.common.util.TclJavaUtils]
  if {[string equal $isExos "true"]} {
   CLI disable clipaging
  } else {
     CLI disable clipaging session
    }
  CLI show vlan
  set liststr [$jTclJavaUtils parseShowVlanContentForVRName ${CLI.OUT} $deviceIP]
  return $liststr
}


##################################################################################
# Returns flattened list of ports for given ports ranges or lists
# eg: getPortsList "1,3-6" will return "1,3,4,5,6"
# eg: getPortsList "1:1,1:3-1:6" will return "1:1,1:3,1:4,1:5,1:6"
# Singals error if given port range is not valid.
##################################################################################

proc getPortsList {portrange} {
  set portlist {}
  set tokens [split $portrange ,]
  foreach range $tokens {
    set range [string trim $range]
    set rangebounds [split $range -]

    if {[llength $rangebounds] == 1 } {
       if {[llength $portlist] == 0} {
          set portlist $rangebounds
       } else {
          set portlist $portlist,$rangebounds
       }
    } else {
        if { [llength $rangebounds] == 2 } {
            set part1 [string trim [lindex $rangebounds 0]]
            set part2 [string trim [lindex $rangebounds 1]]

            # check if format is slot:port or just port
            set portSplit [split $part1 :]
            set portSplit2 [split $part2 :]

            if { [llength $portSplit] == 2  && [llength $portSplit2] == 2} {
              # slot:port case
              set slotNumber [string trim [lindex $portSplit 0]]
              set slotNumber2 [string trim [lindex $portSplit2 0]]

              if {$slotNumber != $slotNumber2} {
                error "$portrange is not valid port range."
              }
              set part1 [string trim [lindex $portSplit 1]]
              set part2 [string trim [lindex $portSplit2 1]]
            } elseif { [llength $portSplit] == 2  || [llength $portSplit2] == 2} {

                if {[llength $portSplit] != 2 } {
                error "$portrange is not valid port range."
              } else {
                set slotNumber [string trim [lindex $portSplit 0]]
                set part1 [string trim [lindex $portSplit 1]]
                set part2 [string trim [lindex $portSplit2 0]]
              }
            }

            if {$part1 > $part2} {
            # todo: spit error only if abort_on_error is not 1
             error "Port range $range invalid."
            }
            while {$part1 <= $part2} {
              set qualifiedPortStr $part1
              if {[info exists slotNumber]} {
                set qualifiedPortStr $slotNumber:$part1
              }



              if {[llength $portlist] == 0} {
                set portlist $qualifiedPortStr
              } else {
                set portlist $portlist,$qualifiedPortStr
              }
               incr part1
            }
        }
     }
  }
  return $portlist
}

##################################################################################
# Returns numbers of ports in the port list or port range specified
##################################################################################
proc getPortCount {portlist} {
  set portlistStr [getPortsList $portlist]
  set tokens [split $portlistStr ,]
  return [llength $tokens]
}

# checks if module is present in show version images command output
proc isXOSFirmwareModulePresent {module} {
  set regex *$module*
  CLI show version images
  ECHO " "

  set a [split ${CLI.OUT} \n]
  foreach imageEntry $a {
  if {[expr [string match -nocase $regex $imageEntry] == 1]} {
    return true
    }
  }
  return false
}


proc getFDBEntriesAsString {} {
  global deviceIP
  global isExos
  package require java
  set jShowFDBParser [java::new com.extremenetworks.epicenter.common.util.ShowFDBParser]
  if {[string equal $isExos "true"]} {
   CLI disable clipaging
  } else {
     CLI disable clipaging session
    }

  CLI show fdb
  set liststr [$jShowFDBParser parseShowFdbContent ${CLI.OUT} $deviceIP]
  return $liststr
}

proc getVRNameAsString {} {
  global deviceIP
  global isExos
  package require java
  set jTclJavaUtils [java::new com.extremenetworks.epicenter.common.util.TclJavaUtils]
  if {[string equal $isExos "true"]} {
   CLI disable clipaging
  } else {
     CLI disable clipaging session
    }
  CLI show vlan
  set liststr [$jTclJavaUtils parseShowVlanContentForVRName ${CLI.OUT} $deviceIP]
  return $liststr
}
