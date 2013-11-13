package com.textteaser.summarizer

import com.google.inject.Inject
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import scala.collection.mutable.ListBuffer

class Summarizer @Inject() (parser: Parser, keywordService: KeywordService) {

  private var _summarySize: Int = 5
  private var _keywordsSize: Int = 10

  def summarySize = _summarySize
  def summarySize_=(newSize: Int) = {
    _summarySize = newSize
  }

  def keywordsSize = _keywordsSize
  def keywordsSize_=(newSize: Int) = {
    _keywordsSize = newSize
  }

  private def ensureSizeDoesNotExceedLimit(size: Int, limit: Int): Int = {
    size.min(limit)
  }

  def summarize(text: String, title: String, link: String, blog: String, category: String) = {
    val sentences = parser.splitSentences(text)
    def titleWords = parser.splitWords(title)
    val resKeywords = parser.getKeywords(text)
    val keywords = resKeywords.keywords
    keywordsSize = ensureSizeDoesNotExceedLimit(keywordsSize, keywords.size)
    val topKeywords = getTopKeywords(keywords.take(keywordsSize), resKeywords.wordCount, link, blog, category)
    val result = computeScore(sentences, titleWords, topKeywords)
    summarySize = ensureSizeDoesNotExceedLimit(summarySize, result.size)
    Summary(result.sortBy(-_.score).take(summarySize).sortBy(_.order).toIndexedSeq)
  }

  def toJSON(summary: Summary) = compact(render("sentences" -> summary.toList))

  def getTopKeywords(keywords: List[ArticleKeyword],
                     articleCount: Int, link: String,
                     blog: String, category: String): List[TopKeyword] =
    keywords.map { k =>
      val blogCount = keywordService.getBlogCount(blog) + 1.0
      val categoryCount = keywordService.getCategoryCount(category) + 1.0

      keywordService.add(k.word, k.count, link, blog, category)

      val articleScore = k.count / articleCount
      val blogScore = keywordService.getBlogScore(k.word, blog) / blogCount
      val categoryScore = keywordService.getCategoryScore(k.word, category) / categoryCount
      val totalScore = articleScore * 1.5 + blogScore + categoryScore

      TopKeyword(k.word, totalScore)
    }

  def computeScore(sentences: Array[String], titleWords: Array[String], topKeywords: List[TopKeyword]) =
    Array.tabulate(sentences.size) { i =>
      val sentence = parser.splitWords(sentences(i))
      val titleFeature = parser.titleScore(titleWords, sentence)
      val sentenceLength = parser.sentenceLength(sentence)
      val sentencePosition = parser.sentencePosition(i, sentences.size)
      val sbsFeature = sbs(sentence, topKeywords)
      val dbsFeature = dbs(sentence, topKeywords)
      val keywordFrequency = (sbsFeature + dbsFeature) / 2.0 * 10.0
      val totalScore = (titleFeature * 1.5 + keywordFrequency * 2.0 + sentenceLength * 0.5 + sentencePosition * 1.0) / 4.0

      Sentence(sentences(i), totalScore, i)
    }

  def sbs(words: Array[String], topKeywords: List[TopKeyword]): Double = {
    if (words.size == 0)
      0
    else {
      val summ = words.map { word =>
        topKeywords.find(_.word == word) match {
          case None => 0
          case Some(x) => x.score
        }
      }.sum

      1.0 / Math.abs(words.size) * summ
    }
  }

  def dbs(words: Array[String], topKeywords: List[TopKeyword]): Double = {
    if (words.size == 0)
      0
    else {
      val res = words.map { word =>
        topKeywords.find(_.word == word) match {
          case None => 0
          case Some(x) => x.score
        }
      }.zipWithIndex.filter(_._1 > 0)


      val summ = res.zip(res.slice(1, res.size)).map { r =>
        (r._1._1 * r._2._1) / Math.pow(r._1._2 - r._2._2, 2)
      }.sum

      val k = words.intersect(topKeywords.map(_.word)).size + 1

      (1.0 / (k * (k + 1.0))) * summ
    }
  }

  def canonical_dbs(words: Array[String], topKeywords: List[TopKeyword]): Double = {
    if (words.size == 0)
      0
    else {
      val res = words.map { word =>
        topKeywords.find(_.word == word) match {
          case None => 0
          case Some(x) => x.score
        }
      }.zipWithIndex.filter(_._1 > 0)


      val summ = res.zip(res.slice(1, res.size)).map { r =>
        (r._1._1 * r._2._1) / Math.pow(r._1._2 - r._2._2, 2)
      }.sum

      val k = words.intersect(topKeywords.map(_.word)).size + 1

      (1.0 / (k * (k + 1.0))) * summ
    }
  }
}

case class TopKeyword(word: String, score: Double)
case class Sentence(sentence: String, score: Double, order: Int)

/*
   * The Density Based Selection (DBS) above is so fucking abstracted.
   * USE THIS FOR REFERENCE:
   *
   * def dbs(sentence, topKeywords) {
		def words = parserService.splitWords sentence
		words.removeAll(" ")
		words = words*.toLowerCase()

		if(words.size == 0)
			return 0

		def k = words.intersect(topKeywords.word).size() + 1
		def summ = 0
		def firstWord = []
		def secondWord = []

		for(def i = 0; i < words.size(); i++) {
			def index = topKeywords.word.indexOf(words[i])

			if(index > -1) {
				def score = topKeywords[index].totalScore

				if(firstWord == []) {
					firstWord = [i: i, score: score]
				}
				else {
					secondWord = firstWord
					firstWord = [i: i, score: score]

					summ += (firstWord.score * secondWord.score) / Math.pow((firstWord.i - secondWord.i), 2)
				}

			}
		}

		def formula = ((1 / k * (k + 1)) * summ) as double

		return formula
	}

	Just for backup, this is for Summation Based Selection (SBS):

	def sbs(sentence, topKeywords) {
		def words = parserService.splitWords sentence
		words.removeAll(" ")

		if(words.size == 0)
			return 0

		def summ = 0

		words.each { word ->
			word = word.toLowerCase()
			def index = topKeywords.word.indexOf(word)
			def score = index == -1 ? 0 : topKeywords[index].totalScore
			summ += score
		}

		def formula = (1 / Math.abs(words.size) * summ) as double

		return formula
	}

   */