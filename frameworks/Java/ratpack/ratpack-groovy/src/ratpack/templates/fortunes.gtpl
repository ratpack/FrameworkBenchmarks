yieldUnescaped '<!DOCTYPE html>'
html {
  head {
    title('Fortunes')
  }
  body {
    table {
      tr {
        th('id')
        th('message')
      }
      model.fortunes.each { fortune ->
        tr {
          td(fortune.id)
          td(com.google.common.html.HtmlEscapers.htmlEscaper().escape(fortune.message))
        }
      }
    }
  }
}
