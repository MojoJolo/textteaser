package com.textteaser.summarizer

import com.google.inject.Inject
import com.textteaser.summarizer.models.Keyword
import com.foursquare.rogue.LiftRogue._
import org.joda.time.DateTime

class KeywordService {
  val controller = "keywords"

  def getBlogCount(blog: String): Long = Keyword.where(_.blog eqs blog).count

  def getCategoryCount(cat: String): Long = Keyword.where(_.category eqs cat).count

  def getBlogScore(word: String, blog: String): Long = Keyword.where(_.word eqs word).and(_.blog eqs blog)
    .fetch.map(_.score._1)
    .sum

  def getCategoryScore(word: String, cat: String): Long = Keyword.where(_.word eqs word).and(_.category eqs cat)
    .fetch.map(_.score._1)
    .sum

  def add(word: String, count: Long, summaryId: String, blog: String, cat: String) = Keyword.createRecord
    .word(word)
    .score(count)
    .summaryId(summaryId)
    .blog(blog)
    .category(cat)
    .date(new DateTime().toDate)
    .save
}