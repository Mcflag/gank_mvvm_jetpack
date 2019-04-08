package com.ccooy.gankart.http.service

import com.ccooy.gankart.entity.*
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 *
 * gank.io接口
 *
 */
interface GankService {

    /**
     * 根据category获取Android、iOS等干货数据
     * @param category  类别
     * @param count     条目数目，大于0
     * @param page      页数，大于0
     */
    @GET("data/{category}/{count}/{page}")
    fun getCategoryData(
        @Path("category") category: String,
        @Path("count") count: Int,
        @Path("page") page: Int
    ): Flowable<CategoryResult>

    /**
     * 获取随机的数据
     * @param category  类别
     * @param count     条目数目，大于0
     */
    @GET("random/data/{category}/{count}")
    fun getRandomData(
        @Path("category") category: String,
        @Path("count") count: Int
    ): Flowable<CategoryResult>

    /**
     * 获取任意一天的数据
     * @param year   年
     * @param month  月
     * @param day    日
     */
    @GET("day/{year}/{month}/{day}")
    fun getOneDayData(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int
    ): Flowable<DayResult>

    /**
     * 获取今日的数据
     */
    @GET("today")
    fun getTodayData(): Flowable<DayResult>

    /**
     * 获取发过干货的日期数据
     */
    @GET("day/history")
    fun getHistoryDay(): Flowable<HistoryDateResult>

    /**
     * 获取特定日期网站数据
     * @param year   年
     * @param month  月
     * @param day    日
     */
    @GET("history/content/day/{year}/{month}/{day}")
    fun getHistoryDayData(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Path("day") day: Int
    ): Flowable<HistoryDayResult>

    /**
     * 获取某几日网站数据
     * @param count     条目数目，大于0
     * @param page      页数，大于0
     */
    @GET("history/content/{count}/{page}")
    fun getHistoryDaysData(
        @Path("count") count: Int,
        @Path("page") page: Int
    ): Flowable<HistoryDayResult>

    /**
     * 搜索接口
     * @param query     搜索关键字
     * @param category  类别：all | Android | iOS | 休息视频 | 福利 | 拓展资源 | 前端 | 瞎推荐 | App
     * @param count     条目数目，大于0，最大50
     * @param page      页数，大于0
     */
    @GET("search/query/{query}/category/{category}/count/{count}/page/{page}")
    fun getQueryData(
        @Path("query") query: String,
        @Path("category") category: String,
        @Path("count") count: Int,
        @Path("page") page: Int
    ): Flowable<QueryResult>

    /**
     * 获取闲读主分类数据
     */
    @GET("xiandu/categories")
    fun getXianDuCategories(): Flowable<XianDuCategoriesResult>

    /**
     * 获取闲读的子分类数据
     * @param category  为主分类返回的en_name,例如[apps, wow, android, iOS]
     */
    @GET("xiandu/category/{category}")
    fun getXianDuCategory(
        @Path("category") category: String
    ): Flowable<XianDuSubCategoryResult>

    /**
     * 获取闲读的数据
     * @param id        子分类返回的id
     * @param count     条目数目，大于0
     * @param page      页数，大于0
     */
    @GET("xiandu/data/id/{id}/count/{count}/page/{page}")
    fun getXianDuData(
        @Path("id") id: String,
        @Path("count") count: Int,
        @Path("page") page: Int
    ): Flowable<XianDuDataResult>

}