package com.textteaser.summarizer

import org.scalatest.{BeforeAndAfter, FunSuite}
import scala.collection.immutable.{VectorBuilder, IndexedSeq}

class SummarySuite extends FunSuite with BeforeAndAfter {

  var singleNonEmptySentenceSeq: IndexedSeq[Sentence] = _
  var twoSentenceSeq: IndexedSeq[Sentence] = _
  var summaryOnEmptySentence: Summary = _
  var summaryOnSingleSentenceSeq: Summary = _
  var summaryOnTwoSentenceSeq: Summary = _
  var targetSummaryForForeach: Summary = _
  var targetVectorForForeach: VectorBuilder[String] = _

  before {
    singleNonEmptySentenceSeq = Vector(Sentence("Hello world", 1d, 1))
    twoSentenceSeq = Vector(Sentence("Hello world", 1d, 1), Sentence("Hello world", 1d, 1))
    summaryOnEmptySentence = Summary(Vector.empty[Sentence])
    summaryOnSingleSentenceSeq = Summary(singleNonEmptySentenceSeq)
    summaryOnTwoSentenceSeq = Summary(twoSentenceSeq)
    targetSummaryForForeach = Summary(Vector(Sentence("a", 1d, 1), Sentence("b", 1d, 1), Sentence("c", 1d, 1)))
    targetVectorForForeach = new VectorBuilder[String]
  }

  test("charCount on an empty sequence") {
    assert(summaryOnEmptySentence.charCount === 0)
  }

  test("""charCount on "Hello world" is 11""") {
    assert(summaryOnSingleSentenceSeq.charCount === 11)
  }

  test("""charCount on 2 x "Hello world" is 22""") {
    assert(summaryOnTwoSentenceSeq.charCount === 22)
  }

  test("""foreach on an non-empty-sentence summary""") {
    targetSummaryForForeach foreach { case s => targetVectorForForeach += s }
    assert(targetVectorForForeach.result === Vector("a", "b", "c"))
  }

  test("""foreach on an empty-sentence summary""") {
    summaryOnEmptySentence foreach { case s => targetVectorForForeach += s }
    assert(targetVectorForForeach.result === Vector())
  }

  test("""takeChars on an empty-sentence summary""") {
    assert(summaryOnEmptySentence.takeChars(100) === summaryOnEmptySentence)
  }

  test("""takeChars on a non-empty-sentence summary""") {
    assert(summaryOnTwoSentenceSeq.takeChars(11) === summaryOnSingleSentenceSeq)
  }

  test("""takeChars on a non-empty-sentence summary with 0 to take""") {
    assert(summaryOnTwoSentenceSeq.takeChars(0) === summaryOnEmptySentence)
  }

  test("""Summary constructor itself""") {
    assert(Summary(Vector(Sentence("Hello world", 1d, 1))) === summaryOnSingleSentenceSeq)
  }
}
