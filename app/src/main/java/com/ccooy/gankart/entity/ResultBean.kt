package com.ccooy.gankart.entity

/**
 * Category 返回model
 */
data class CategoryResult(
    var error: Boolean,
    var results: List<ResultsBean> = emptyList()
)

data class DayResult(
    var category: List<String>,
    var error: Boolean,
    var results: HashMap<String, List<ResultsBean>> = HashMap()
)

data class ResultsBean(
    var _id: String,
    var createdAt: String,
    var desc: String,
    var publishedAt: String,
    var source: String,
    var type: String,
    var url: String,
    var used: Boolean,
    var who: String,
    var images: List<String> = emptyList()
)

data class HistoryDateResult(
    var error: Boolean,
    var results: List<String> = emptyList()
)

data class HistoryDayResult(
    var error: Boolean,
    var results: List<ResultsWebBean> = emptyList()
)

data class ResultsWebBean(
    var _id: String,
    var content: String,
    var createdAt: String,
    var publishedAt: String,
    var rand_id: String,
    var title: String,
    var updated_at: String
)

data class QueryResult(
    var count: Int,
    var error: Boolean,
    var results: List<QueryBean> = emptyList()
)

data class QueryBean(
    var ganhuo_id: String,
    var desc: String,
    var publishedAt: String,
    var readability: String,
    var type: String,
    var url: String,
    var who: String
)

data class XianDuCategoriesResult(
    var error: Boolean,
    var results: List<XianDuCategoryBean> = emptyList()
)

data class XianDuCategoryBean(
    var _id: String,
    var en_name: String,
    var name: String,
    var rank: Int
)

data class XianDuSubCategoryResult(
    var error: Boolean,
    var results: List<XianDuSubCategoryBean> = emptyList()
)

data class XianDuSubCategoryBean(
    var _id: String,
    var created_at: String,
    var icon: String,
    var id: String,
    var title: String
)

data class XianDuDataResult(
    var error: Boolean,
    var results: List<XianDuDataBean> = emptyList()
)

data class XianDuDataBean(
    var _id: String,
    var content: String,
    var cover: String,
    var crawled: Long,
    var created_at: String,
    var deleted: Boolean,
    var published_at: String,
    var raw: String,
    var site: SiteBean,
    var title: String,
    var uid: String,
    var url: String
)

data class SiteBean(
    var cat_cn: String,
    var cat_en: String,
    var desc: String,
    var feed_id: String,
    var icon: String,
    var id: String,
    var name: String,
    var subscribers: Int,
    var type: String,
    var url: String
)