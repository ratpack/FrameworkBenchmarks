import subprocess
import sys
import os
import setup_util

def start(args, logfile, errfile):
  # TODO
  # setup_util.replace_text("ratpack/src/ratpack/DataSource.groovy", "localhost", args.database_host)
  
  install_command = "gradlew clean installApp"
  run_command = "ratpack-groovy.bat"
  if os.name != 'nt':
    install_command = "./" + install_command
    run_command = "./ratpack-groovy"
  
  try:
    subprocess.check_call(install_command, shell=True, cwd="ratpack-groovy", stderr=errfile, stdout=logfile)
    subprocess.check_call(run_command, shell=True, cwd="ratpack-groovy/build/install/ratpack-groovy/bin", stderr=errfile, stdout=logfile)
    return 0
  except subprocess.CalledProcessError:
    return 1

def stop(logfile, errfile):
  try:
    if os.name == 'nt':
      subprocess.check_call("wmic process where \"CommandLine LIKE '%ratpack-groovy%'\" call terminate", stderr=errfile, stdout=logfile)
      return 0
    p = subprocess.Popen(['ps', 'aux'], stdout=subprocess.PIPE)
    out, err = p.communicate()
    for line in out.splitlines():
      if 'ratpack-groovy' in line:
        pid = int(line.split(None, 2)[1])
        os.kill(pid, 9)
    return 0
  except subprocess.CalledProcessError:
    return 1
