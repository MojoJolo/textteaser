package com.textteaser.summarizer

import scala.collection.immutable.IndexedSeq

case class Summary(results: IndexedSeq[Sentence]) extends Traversable[String] {
  lazy val charCount = if (results.isEmpty) 0 else results.map(_.sentence.size).sum

  def foreach[U](f: String => U) {
    results.foreach( s => f(s.sentence) )
  }

  def takeChars(limitCharCount: Int): Summary = {
    var count = 0
    // not beautiful, but does the job
    val newSentences = results.takeWhile { sentence =>
      count += sentence.sentence.size
      limitCharCount >= count
    }
    Summary(newSentences)
  }
}