package powerrangers.eivom.feature_movie.domain.utility

import java.time.LocalDate

sealed class MovieFilter(val value: Any, val isAndLogic: Boolean? = null) {
    class Trending(isDayTrending: Boolean): MovieFilter(isDayTrending)

    class IncludeAdult(value: Boolean): MovieFilter(value)
//    class Language(value: String): MovieFilter(value) Translate to corresponding language
    class ReleaseYear(value: String): MovieFilter(value)
    class MinimumReleaseDate(value: LocalDate): MovieFilter(value)
    class MaximumReleaseDate(value: LocalDate): MovieFilter(value)
    class MinimumRating(value: Float): MovieFilter(value)
    class MaximumRating(value: Float): MovieFilter(value)
    class MinimumVote(value: Int): MovieFilter(value)
    class MaximumVote(value: Int): MovieFilter(value)
//    class Cast(value: List<String>): MovieFilter(value)
//    class Crew(value: List<String>): MovieFilter(value)
//    class People(value: List<String>): MovieFilter(value)
//    class Company(value: List<String>): MovieFilter(value)
    class Genre(value: List<Int>, logic: Boolean): MovieFilter(value, logic)
    class OriginCountry(value: List<String>, isAndLogic: Boolean): MovieFilter(value, isAndLogic)
    class OriginLanguage(value: List<String>, isAndLogic: Boolean): MovieFilter(value, isAndLogic)
//    class ReleaseType
    class MinimumLength(value: Int): MovieFilter(value)
    class MaximumLength(value: Int): MovieFilter(value)

//    class WithoutCompany(value: List<String>): MovieFilter(value)
    class WithoutGenre(value: List<Int>, isAndLogic: Boolean): MovieFilter(value, isAndLogic)
}