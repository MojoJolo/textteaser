package com.textteaser.summarizer

trait KeywordService {
  def getBlogCount(blog: String): Long
  def getCategoryCount(cat: String): Long
  def getBlogScore(word: String, blog: String): Long
  def getCategoryScore(word: String, cat: String): Long
  def add(word: String, count: Long, summaryId: String, blog: String, cat: String): Unit
}