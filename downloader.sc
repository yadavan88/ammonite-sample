#!/usr/bin/env amm

import $ivy.`com.lihaoyi::ujson:0.9.6`
import $ivy.`com.lihaoyi::upickle:1.3.8`, upickle.default._
import $ivy.`com.lihaoyi::requests:0.6.5`
import $ivy.`com.lihaoyi::ammonite-ops:2.3.8`

import upickle.default.{macroRW, ReadWriter}

case class Country(
    iso: String,
    country: String,
    capital: String,
    continent: String,
    languages: Seq[String]
)

object Country {
  implicit val rw: ReadWriter[Country] = macroRW
}

val url =
  "https://raw.githubusercontent.com/mapbox/model-un/master/data/countries.json"

@main
def countriesList(
    @doc("Pass continent code")
    continent: String
): Unit = {
  val request = requests.get(url)
  val countries = read[Seq[Country]](request.text)

  val countriesWithEnglish =
    if (continent.nonEmpty)
      countries
        .filter(c => c.languages.contains("en"))
        .filter(c => continent contains c.continent)
    else countries.filter(c => c.languages.contains("en"))

  println(countriesWithEnglish)
  val jsonToWrite = write(countriesWithEnglish, 4)

  println(jsonToWrite)
  import  ammonite.ops._
  val downloadPath = pwd / 'target / "downloaded-files"
  rm ! downloadPath
  write(downloadPath/"countries.json", jsonToWrite, createFolders = true)
}
