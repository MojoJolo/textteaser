package com.textteaser.summarizer

case class Article(id: String,
  title: String,
  article: String,
  url: String = "",
  blog: String = "",
  category: String = "")