package com.textteaser.summarizer

class Config {
  
  def lang = "EN"
  
  object words {
    def ideal = 20
  }

  object db {
    def host = "localhost"
    def port = 27017
    def name = "tt_db"
    def username = ""
    def password = ""
  }
}