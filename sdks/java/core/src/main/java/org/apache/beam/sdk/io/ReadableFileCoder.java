/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.beam.sdk.coders.AtomicCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.io.fs.MatchResult;

/** A {@link Coder} for {@link FileIO.ReadableFile}. */
public class ReadableFileCoder extends AtomicCoder<FileIO.ReadableFile> {
  private static final ReadableFileCoder INSTANCE = new ReadableFileCoder();
  private final AtomicCoder<Metadata> metadataCoder;

  /** Returns the instance of {@link ReadableFileCoder}. */
  public static ReadableFileCoder of() {
    this.metadataCoder = MetadataCoder.of();
    return INSTANCE;
  }

  public static ReadableFileCoder of(AtomicCoder<Metadata> metadataCoder) {
    this.metadataCoder = metadataCoder;
    return INSTANCE;
  }

  @Override
  public void encode(FileIO.ReadableFile value, OutputStream os) throws IOException {
    metadataCoder.encode(value.getMetadata(), os);
    VarIntCoder.of().encode(value.getCompression().ordinal(), os);
  }

  @Override
  public FileIO.ReadableFile decode(InputStream is) throws IOException {
    MatchResult.Metadata metadata = metadataCoder.decode(is);
    Compression compression = Compression.values()[VarIntCoder.of().decode(is)];
    return new FileIO.ReadableFile(metadata, compression);
  }
}
