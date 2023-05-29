/*
 *  Copyright 2013 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.webrtc;

/**
 * Representation of a single ICE Candidate, mirroring
 * {@code IceCandidateInterface} in the C++ API.
 */
public class IceCandidate {
  public final String sdpMid;
  public final int sdpMLineIndex;
  public final String candidate;

  public IceCandidate(String sdpMid, int sdpMLineIndex, String candidate) {
    this.sdpMid = sdpMid;
    this.sdpMLineIndex = sdpMLineIndex;
    this.candidate = candidate;

  }

  @CalledByNative
  IceCandidate(String sdpMid, int sdpMLineIndex, String candidate, String serverUrl) {
    this.sdpMid = sdpMid;
    this.sdpMLineIndex = sdpMLineIndex;
    this.candidate = candidate;
  }

  @Override
  public String toString() {
    return sdpMid + ":" + sdpMLineIndex + ":" + candidate + ":";
  }

  @CalledByNative
  String getSdpMid() {
    return sdpMid;
  }

}
