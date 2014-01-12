import subprocess
import sys
import os
import setup_util

def start(args, logfile, errfile):
  try:
    subprocess.check_call("./gradlew clean ratpack-groovy:runInBackground", shell=True, cwd="ratpack", stderr=errfile, stdout=logfile)
    return 0
  except subprocess.CalledProcessError:
    return 1

def stop(logfile, errfile):
  try:
    p = subprocess.Popen(['ps', 'aux'], stdout=subprocess.PIPE)
    out, err = p.communicate()
    for line in out.splitlines():
      if 'ratpack.groovy.launch.GroovyRatpackMain' in line:
        pid = int(line.split(None, 2)[1])
        os.kill(pid, 9)
    return 0
  except subprocess.CalledProcessError:
    return 1
