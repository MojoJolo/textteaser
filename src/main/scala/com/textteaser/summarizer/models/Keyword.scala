package com.textteaser.summarizer.models

import net.liftweb.mongodb.record.field._
import net.liftweb.record.field._
import net.liftweb.mongodb.record._

class Keyword extends MongoRecord[Keyword] with ObjectIdPk[Keyword] {
  def meta = Keyword

  object word extends StringField(this, "")
  object score extends LongField(this, 0)
  object date extends DateField(this)
  object summaryId extends StringField(this, 10)
  object blog extends StringField(this, "Undefined")
  object category extends StringField(this, "Undefined")
}

object Keyword extends Keyword with MongoMetaRecord[Keyword]