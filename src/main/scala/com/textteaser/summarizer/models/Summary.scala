package com.textteaser.summarizer.models

import net.liftweb.mongodb.record.field._
import net.liftweb.record.field._
import net.liftweb.mongodb.record._

class Summary extends MongoRecord[Summary] with ObjectIdPk[Summary] {
  def meta = Summary

  object summaryId extends StringField(this, 10)
  object title extends StringField(this, "")
  object summary extends StringField(this, "")
  object status extends StringField(this, "Pending")
  object url extends StringField(this, "")
  object blog extends StringField(this, "Undefined")
  object category extends StringField(this, "Undefined")
}

object Summary extends Summary with MongoMetaRecord[Summary]