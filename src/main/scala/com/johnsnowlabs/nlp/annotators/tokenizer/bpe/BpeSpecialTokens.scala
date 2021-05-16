/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.johnsnowlabs.nlp.annotators.tokenizer.bpe

import scala.collection.immutable.HashSet

// TODO: How to do this properly?
//private[nlp] class BpeSpecialTokens(modelType: String, vocab: Map[String, Int]) {
//  val availableModels: Array[String] = Array("roberta")
//
//  def getSentencePadding: (String, String) =
//    modelType match {
//      case "roberta" => ("<s>", "</s>")
//    }
//
//  def getSpecialTokens: SpecialTokens =
//    modelType match {
//      case "roberta" => SpecialTokens.getRobertaSpecialTokens(vocab)
//    }
//}
private[nlp] case class SpecialTokens(
                                       vocab: Map[String, Int],
                                       startTokenString: String,
                                       endTokenString: String,
                                       unkTokenString: String,
                                       maskTokenString: String,
                                       padTokenString: String,
                                     ) {
  for (specialTok <- Array(startTokenString, endTokenString, unkTokenString, maskTokenString, padTokenString))
    require(vocab.contains(specialTok), s"Special Token '$specialTok' needs to be in vocabulary.")
  val sentenceStart: SpecialToken = SpecialToken(startTokenString, vocab(startTokenString))
  val sentenceEnd: SpecialToken = SpecialToken(endTokenString, vocab(endTokenString))
  val unk: SpecialToken = SpecialToken(unkTokenString, vocab(unkTokenString))
  val mask: SpecialToken = SpecialToken(
    maskTokenString,
    vocab(maskTokenString),
    lstrip = true //TODO: check if should be done for every model
  )
  val pad: SpecialToken = SpecialToken(padTokenString, vocab(padTokenString))

  val allTokens: Set[SpecialToken] = Set(sentenceStart, sentenceEnd, unk, mask, pad)

  def contains(s: String): Boolean = allTokens.contains(SpecialToken(content = s, id = 0))
}

//private object SpecialTokens {
//  def getRobertaSpecialTokens(vocab: Map[String, Int]): SpecialTokens = SpecialTokens(
//    SpecialToken(
//      content = "<s>",
//      id = vocab("<s>"),
//    ),
//    SpecialToken(
//      content = "</s>",
//      id = vocab("</s>"),
//    ),
//    SpecialToken(
//      content = "<unk>",
//      id = vocab("<unk>"),
//    ),
//    SpecialToken(
//      content = "<pad>",
//      id = vocab("<pad>"),
//    ),
//    SpecialToken(
//      content = "<mask>",
//      id = vocab("<mask>"),
//      lstrip = true
//    ),
//  )
//}

private[nlp] case class SpecialToken(
                                      content: String,
                                      id: Int,
                                      singleWord: Boolean = false,
                                      lstrip: Boolean = false,
                                      rstrip: Boolean = false,
                                    ) {
  //  implicit def convertToString(s: SpecialToken): String = s.content
  override def hashCode(): Int = content.hashCode

  override def canEqual(that: Any): Boolean = that.isInstanceOf[SpecialToken]

  override def equals(obj: Any): Boolean = obj match {
    case obj: SpecialToken => obj.content == content
    case _ => false
  }

  override def toString: String = content
}