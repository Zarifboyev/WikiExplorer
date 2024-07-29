package com.z99a.zarifboyevjavohir.vikipediya.data.model

data class WikipediaResponse(
    val query: Query
) {

    data class Query(
        val pages: Map<String, Page>
    ) {

        data class Page(
            val pageprops: PageProps?
        )

        data class PageProps(
            val wikibase_item: String?
        )
    }
}
