package com.tanasi.mangajap.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tanasi.mangajap.adapters.viewHolders.*
import com.tanasi.mangajap.databinding.*
import com.tanasi.mangajap.models.*

class MangaJapAdapter(
        private val items: List<Item>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface Item {
        var typeLayout: Type
    }

    enum class Type {
        ANIME_SEARCH,
        ANIME_SEARCH_ADD,
        ANIME_TRENDING,

        ANIME_HEADER,
        ANIME_SUMMARY,
        ANIME_PROGRESSION,
        ANIME_REVIEWS,

        ANIME_ENTRY_LIBRARY,
        ANIME_ENTRY_PREVIEW,
        ANIME_ENTRY_TO_WATCH,

        MANGA_SEARCH,
        MANGA_SEARCH_ADD,
        MANGA_TRENDING,

        MANGA_HEADER,
        MANGA_HEADER_SUMMARY,
        MANGA_HEADER_PROGRESSION,
        MANGA_HEADER_REVIEWS,

        MANGA_ENTRY_LIBRARY,
        MANGA_ENTRY_PREVIEW,
        MANGA_ENTRY_TO_READ,

        VOLUME_MANGA,
        VOLUME_MANGA_DETAILS,

        EPISODE_ANIME,
        SEASON_ANIME,
        EPISODE_ANIME_HEADER,
        USER,
        FOLLOWERS,
        FOLLOWING,
        PEOPLE_DISCOVER,
        STAFF_PEOPLE,
        BOOK_DETAILS,
        BOOK,
        BOOK_PAGE,

        HEADER_AGENDA,
        HEADER_LIBRARY_STATUS,

        FOLDER,
        LOAD_MORE,
        REVIEW,
        REVIEW_HEADER,
        STATS_PREVIEW_MANGA_FOLLOWED,
        STATS_PREVIEW_MANGA_VOLUMES,
        STATS_PREVIEW_MANGA_CHAPTERS,
        STATS_PREVIEW_ANIME_FOLLOWED,
        STATS_PREVIEW_ANIME_TIME_SPENT,
        STATS_PREVIEW_ANIME_EPISODES,
        AD_DISCOVER,
        AD_SEARCH,
    }

    private var onLoadMoreListener: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (Type.values()[viewType]) {
        Type.AD_DISCOVER -> VhAd(ItemAdDiscoverBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.AD_SEARCH -> VhAd(ItemAdSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.ANIME_SEARCH -> VhAnime(ItemMediaSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.ANIME_SEARCH_ADD -> VhAnime(ItemMediaSearchAddBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.ANIME_HEADER -> VhAnime(ItemAnimeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.ANIME_SUMMARY -> VhAnime(ItemAnimeSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.ANIME_PROGRESSION -> VhAnime(ItemAnimeProgressionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.ANIME_REVIEWS -> VhAnime(ItemAnimeReviewsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.ANIME_TRENDING -> VhAnime(ItemMediaTrendingBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.ANIME_ENTRY_LIBRARY -> VhAnimeEntry(ItemMediaLibraryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.ANIME_ENTRY_PREVIEW -> VhAnimeEntry(ItemMediaPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.EPISODE_ANIME_HEADER -> VhEpisode(ItemEpisodeAnimeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.EPISODE_ANIME -> VhEpisode(ItemEpisodeAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.MANGA_SEARCH -> VhManga(ItemMediaSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.MANGA_SEARCH_ADD -> VhManga(ItemMediaSearchAddBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.MANGA_HEADER -> VhManga(ItemMangaHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.MANGA_HEADER_SUMMARY -> VhManga(ItemMangaSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.MANGA_HEADER_PROGRESSION -> VhManga(ItemMangaProgressionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.MANGA_HEADER_REVIEWS -> VhManga(ItemMangaReviewsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.MANGA_TRENDING -> VhManga(ItemMediaTrendingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.MANGA_ENTRY_LIBRARY -> VhMangaEntry(ItemMediaLibraryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.MANGA_ENTRY_PREVIEW -> VhMangaEntry(ItemMediaPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.SEASON_ANIME -> VhSeason(ItemSeasonAnimeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.VOLUME_MANGA -> VhVolume(ItemVolumeMangaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.VOLUME_MANGA_DETAILS -> VhVolume(ItemVolumeMangaDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.REVIEW_HEADER -> VhReview(ItemReviewHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.REVIEW -> VhReview(ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.USER -> VhUser(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.FOLLOWERS -> VhFollow(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.FOLLOWING -> VhFollow(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.STATS_PREVIEW_MANGA_FOLLOWED,
        Type.STATS_PREVIEW_MANGA_VOLUMES,
        Type.STATS_PREVIEW_MANGA_CHAPTERS,
        Type.STATS_PREVIEW_ANIME_FOLLOWED,
        Type.STATS_PREVIEW_ANIME_EPISODES -> VhUserStats(ItemStatsPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.STATS_PREVIEW_ANIME_TIME_SPENT -> VhUserStats(ItemStatsTimeSpentPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.PEOPLE_DISCOVER -> VhPeople(ItemPeopleDiscoverBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.STAFF_PEOPLE -> VhStaff(ItemStaffPeopleBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.BOOK_DETAILS -> VhBook(ItemBookDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.BOOK -> VhBook(ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.BOOK_PAGE -> VhBookPage(ItemReadingsBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.FOLDER -> VhFolder(ItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.LOAD_MORE -> VhLoadMore(ItemLoadMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.HEADER_AGENDA -> VhHeader(ItemAgendaHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.HEADER_LIBRARY_STATUS -> VhHeader(ItemLibraryStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        Type.ANIME_ENTRY_TO_WATCH -> VhAnimeEntry(ItemAnimeToWatchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        Type.MANGA_ENTRY_TO_READ -> VhMangaEntry(ItemMangaToReadBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onLoadMoreListener?.let { onLoadMoreListener ->
            when (val loadMore = items.last()) {
                is LoadMore -> {
                    if (position >= itemCount - 3 && !loadMore.isLoading && loadMore.isMoreDataAvailable) {
                        onLoadMoreListener()
                        loadMore.isLoading = true
                    }
                }
            }
        }

        when (holder) {
            is VhAd -> holder.setVhAd(items[position] as Ad)
            is VhAnime -> holder.setVhAnime(items[position] as Anime)
            is VhAnimeEntry -> holder.setVhAnimeEntry(items[position] as AnimeEntry)
            is VhBook -> holder.setVhBooks(items[position] as Book)
            is VhBookPage -> holder.setVhBookPage(items[position] as BookPage)
            is VhEpisode -> holder.setVhEpisode(items[position] as Episode)
            is VhFolder -> holder.setFolder(items[position] as Folder)
            is VhFollow -> holder.setVhFollow(items[position] as Follow)
            is VhLoadMore -> holder.setLoadMore(items[position] as LoadMore)
            is VhManga -> holder.setVhManga(items[position] as Manga)
            is VhMangaEntry -> holder.setVhMangaEntry(items[position] as MangaEntry)
            is VhPeople -> holder.setVhPeople(items[position] as People)
            is VhReview -> holder.setVhReview(items[position] as Review)
            is VhSeason -> holder.setVhSeason(items[position] as Season)
            is VhStaff -> holder.setVhStaff(items[position] as Staff)
            is VhHeader -> holder.setVhStatusHeader(items[position] as Header)
            is VhUser -> holder.setVhUser(items[position] as User)
            is VhUserStats -> holder.setVhUserStats(items[position] as UserStats)
            is VhVolume -> holder.setVhVolume(items[position] as Volume)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = items[position].typeLayout.ordinal


    fun setOnLoadMoreListener(onLoadMoreListener: () -> Unit) {
        this.onLoadMoreListener = onLoadMoreListener
    }
}