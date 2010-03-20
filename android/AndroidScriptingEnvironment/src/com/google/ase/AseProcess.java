/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.ase;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import android.os.Process;

public class AseProcess {

  protected Integer mShellPid;
  protected FileDescriptor mShellFd;
  protected FileOutputStream mShellOut;
  protected FileInputStream mShellIn;

  protected PrintStream mOut;
  protected Reader mIn;

  public AseProcess() {
  }

  public Integer getPid() {
    return mShellPid;
  }

  public FileDescriptor getFd() {
    return mShellFd;
  }

  public PrintStream getOut() {
    return mOut;
  }

  public PrintStream getErr() {
    return getOut();
  }

  public Reader getIn() {
    return mIn;
  }

  public void error(Object obj) {
    println(obj);
  }

  public void print(Object obj) {
    getOut().print(obj);
  }

  public void println(Object obj) {
    getOut().println(obj);
  }

  public void start(String binary, String arg1, String arg2) {
    int[] pid = new int[1];
    mShellFd = Exec.createSubprocess(binary, arg1, arg2, pid);
    mShellPid = pid[0];

    mShellOut = new FileOutputStream(mShellFd);
    mShellIn = new FileInputStream(mShellFd);

    mOut = new PrintStream(mShellOut, true /* autoflush */);
    mIn = new BufferedReader(new InputStreamReader(mShellIn));

    new Thread(new Runnable() {
      public void run() {
        AseLog.v("Waiting for: " + mShellPid);
        int result = Exec.waitFor(mShellPid);
        AseLog.v("Subprocess exited: " + result);
      }
    }).start();
  }

  public void kill() {
    if (mShellPid != null) {
      Process.killProcess(mShellPid);
    }
  }
}