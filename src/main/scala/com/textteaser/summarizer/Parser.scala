package com.textteaser.summarizer

import opennlp.tools.sentdetect._
import com.google.inject.Inject
import com.google.common.base.{CharMatcher, Splitter}
import scala.collection.JavaConverters

class Parser @Inject() (sentenceDetector: SentenceDetectorME, stopWordList: StopWords, config: Config) {

  val ideal = config.words.ideal
  lazy val stopWords = stopWordList.stopWords

  /*
   * Sentence Length: Computed using this formula
   * (ideal - Math.abs(ideal - words.size)) / ideal
   */
  def sentenceLength(sentence: Array[String]) = 1 - (Math.abs(ideal - sentence.size) / ideal.toDouble)

  /*
   * Split Words: Split words via white space and new lines. Then remove whites space in the resulting array.
   */
  def splitWords(source: String) = JavaConverters.iterableAsScalaIterableConverter(
    Splitter.on("""[^\w]""".r.pattern)
    .trimResults().omitEmptyStrings()
    .split(source)).asScala.toArray

  def titleScore(titleWords: Array[String], sentence: Array[String]) =
    sentence.count(w => !stopWords.contains(w) && titleWords.contains(w)) / titleWords.size.toDouble

  def getKeywords(text: String): KeywordList = {
    val keyWords = splitWords(text)
    val sizeWithRepeatingWords = keyWords.length
    KeywordList(
      keyWords.filterNot(w => stopWords.contains(w))
      .groupBy(w => w)
      .map(w => ArticleKeyword(w._1, w._2.length))
      .toList.sortBy(-_.count),
      sizeWithRepeatingWords)
  }

  def splitSentences(source: String) = sentenceDetector.sentDetect(source)

  def sentencePosition(ctr: Int, sentenceCount: Double) = {
    val normalized = ctr / sentenceCount

    if(normalized > 1.0)
      0d
    else if (normalized > 0.9)
      0.15
    else if (normalized > 0.8)
      0.04
    else if (normalized > 0.7) 
      0.04
    else if (normalized > 0.6) 
      0.06
    else if (normalized > 0.5) 
      0.04
    else if (normalized > 0.4) 
      0.05
    else if (normalized > 0.3) 
      0.08
    else if (normalized > 0.2) 
      0.14
    else if (normalized > 0.1) 
      0.23
    else if (normalized > 0) 
      0.17
    0d
  }
}

case class ArticleKeyword(word: String, count: Int)
case class KeywordList(keywords: List[ArticleKeyword], wordCount: Int)
