package com.example.cuongcaov.comicbook.model

/**
 * Copyright © 2017 Asian Tech Co., Ltd.
 * Created by cuongcaov on 27/09/2017
 */
enum class ComicType(var typeId: Int, var typeName: String) {
    Action(1, "Action"),
    Adult(2, "Adult"),
    Adventure(3, "Adventure"),
    Comedy(4, "Comedy"),
    Cooking(5, "Cooking"),
    Drama(6, "Drama"),
    Ecchi(7, "Ecchi"),
    Fantasy(8, "Fantasy"),
    GenderBender(9, "Gender Bender"),
    Harem(10, "Harem"),
    Historical(11, "Historical"),
    Horror(12, "Horror"),
    Josei(13, "Josei"),
    Manhua(14, "Manhua"),
    Manhwa(15, "Manhwa"),
    MartialArts(16, "Martial Arts"),
    Mature(17, "Mature"),
    Mecha(18, "Mecha"),
    Music(19, "Music"),
    Mystery(20, "Mystery"),
    OneShot(21, "One Shot"),
    Psychological(22, "Psychological"),
    Romance(23, "Romance"),
    SchoolLife(24, "School Life"),
    Scifi(25, "Sci-fi"),
    Seinen(26, "Seinen"),
    Shoujo(27, "Shoujo"),
    ShoujoAi(28, "Shoujo-ai"),
    Shounen(29, "Shounen"),
    ShounenAi(30, "Shounen-ai"),
    SliceofLife(31, "Slice of Life"),
    SoftYaoi(32, "Soft Yaoi"),
    SoftYuri(33, "Soft Yuri"),
    Sports(34, "Sports"),
    Supernatural(35, "Supernatural"),
    Tragedy(36, "Tragedy"),
    Anime(37, "Anime"),
    Comic(38, "Comic"),
    Doujinshi(39, "Doujinshi"),
    Liveaction(40, "Live action"),
    Magic(41, "Magic"),
    manga(42, "manga"),
    NauAn(43, "Nấu Ăn"),
    Smut(44, "Smut"),
    TapChiTruyenTranh(45, "Tạp chí truyện tranh"),
    Trap(46, "Trap (Crossdressing)"),
    TrinhTham(47, "Trinh Thám"),
    TruyenScan(48, "Truyện scan"),
    VideoClip(49, "Video Clip"),
    VnComic(50, "VnComic"),
    Webtoon(51, "Webtoon"),
    Incest(52, "Incest"),
    Yaoi(53, "Yaoi"),
    XuyenKhong(54, "Xuyên Không");

    companion object {
        fun getTypeId(typeName: String): Int? {
            ComicType.values().forEach {
                if (it.typeName == typeName) {
                    return it.typeId
                }
            }
            return null
        }

        fun getTypeName(typeId: Int): String? {
            ComicType.values().forEach {
                if (it.typeId == typeId) {
                    return it.typeName
                }
            }
            return null
        }
    }

}
