package com.tanasi.mangajap.fragments.anime

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.tanasi.jsonapi.JsonApiResponse
import com.tanasi.mangajap.R
import com.tanasi.mangajap.adapters.MangaJapAdapter
import com.tanasi.mangajap.databinding.FragmentAnimeBinding
import com.tanasi.mangajap.databinding.PopupAnimeBinding
import com.tanasi.mangajap.fragments.recyclerView.RecyclerViewFragment
import com.tanasi.mangajap.models.Anime
import com.tanasi.mangajap.models.AnimeEntry
import com.tanasi.mangajap.models.User
import com.tanasi.mangajap.utils.extensions.contains
import com.tanasi.mangajap.utils.extensions.setToolbar
import com.tanasi.mangajap.utils.extensions.shareText
import com.tanasi.mangajap.utils.preferences.UserPreference

class AnimeFragment : Fragment() {

    private var _binding: FragmentAnimeBinding? = null
    private val binding: FragmentAnimeBinding get() = _binding!!

    val viewModel: AnimeViewModel by viewModels()

    private val args: AnimeFragmentArgs by navArgs()

    private lateinit var anime: Anime
    var showSeason: MutableList<Int> = mutableListOf()

    private var animeAboutList: MutableList<MangaJapAdapter.Item> = mutableListOf()
    private var animeEpisodesList: MutableList<MangaJapAdapter.Item> = mutableListOf()

    private val animeAboutFragment = RecyclerViewFragment()
    private val animeEpisodesFragment = RecyclerViewFragment()

    private var fragmentList: MutableList<Fragment> = mutableListOf()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnimeBinding.inflate(inflater, container, false)
        viewModel.getAnime(args.animeId)
        animeAboutFragment.setList(animeAboutList, LinearLayoutManager(requireContext()))
        animeEpisodesFragment.setList(animeEpisodesList, LinearLayoutManager(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(args.animeTitle, "")
        setHasOptionsMenu(true)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                AnimeViewModel.State.Loading -> binding.isLoading.cslIsLoading.visibility = View.VISIBLE
                is AnimeViewModel.State.SuccessLoading -> {
                    anime = state.anime
                    displayAnime()
                    binding.isLoading.cslIsLoading.visibility = View.GONE
                }
                is AnimeViewModel.State.FailedLoading -> when (state.error) {
                    is JsonApiResponse.Error.ServerError -> Toast.makeText(requireContext(), getString(R.string.serverError), Toast.LENGTH_SHORT).show()
                    is JsonApiResponse.Error.NetworkError -> Toast.makeText(requireContext(), getString(R.string.serverError), Toast.LENGTH_SHORT).show()
                    is JsonApiResponse.Error.UnknownError -> Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                }

                AnimeViewModel.State.Updating -> binding.isUpdating.cslIsUpdating.visibility = View.VISIBLE
                is AnimeViewModel.State.SuccessUpdating -> {
                    anime.animeEntry = state.animeEntry
                    displayAnime()
                    binding.isUpdating.cslIsUpdating.visibility = View.GONE
                }
                AnimeViewModel.State.UpdatingForAdding -> {
                    binding.addAnime.setOnClickListener(null)
                    binding.addAnimeImageView.visibility = View.GONE
                    binding.addAnimeProgressBar.visibility = View.VISIBLE
                }
                is AnimeViewModel.State.SuccessUpdatingForAdding -> {
                    binding.addAnimeImageView.apply {
                        visibility = View.VISIBLE
                        setImageResource(R.drawable.ic_check_black_24dp)
                    }
                    binding.addAnimeProgressBar.visibility = View.GONE
                    binding.addAnimeTextView.text = getString(R.string.added_to_library)
                    anime.animeEntry = state.animeEntry
                    Handler(Looper.getMainLooper()).postDelayed({
                        displayAnime()
                    }, 1 * 1000.toLong())
                }
                is AnimeViewModel.State.FailedUpdating -> when (state.error) {
                    is JsonApiResponse.Error.ServerError -> Toast.makeText(requireContext(), getString(R.string.serverError), Toast.LENGTH_SHORT).show()
                    is JsonApiResponse.Error.NetworkError -> Toast.makeText(requireContext(), getString(R.string.serverError), Toast.LENGTH_SHORT).show()
                    is JsonApiResponse.Error.UnknownError -> Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (this::anime.isInitialized) {
            anime.animeEntry?.let { animeEntry ->
                if (animeEntry.isAdd) inflater.inflate(R.menu.menu_anime_activity, menu)
                else inflater.inflate(R.menu.menu_share, menu)
            } ?: let {
                inflater.inflate(R.menu.menu_share, menu)
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.more -> {
                displayPopupWindow()
                return true
            }
            R.id.share -> {
                shareText(getString(R.string.shareAnime, anime.canonicalTitle))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun displayAnime() {
        activity?.invalidateOptionsMenu()

        binding.animeEntryProgressProgressBar.apply {
            anime.animeEntry?.let { animeEntry ->
                progress = animeEntry.getProgress(anime)
                progressTintList = ContextCompat.getColorStateList(requireContext(), animeEntry.getProgressColor(anime))
            }
        }

        binding.addAnime.apply {
            anime.animeEntry?.let { animeEntry ->
                if (animeEntry.isAdd) {
                    visibility =  View.GONE
                } else {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        viewModel.updateAddingAnimeEntry(animeEntry.apply {
                            putAdd(true)
                        })
                    }
                }
            } ?: also {
                visibility = View.VISIBLE
                setOnClickListener {
                    viewModel.createAddAnimeEntry(AnimeEntry().also {
                        it.putAdd(true)
                        it.putStatus(AnimeEntry.Status.watching)
                        it.putUser(User().apply { id = UserPreference(requireContext()).selfId })
                        it.putAnime(anime)
                    })
                }
            }
        }

        binding.addAnimeImageView.setImageResource(R.drawable.ic_add_black_24dp)

        binding.addAnimeProgressBar.visibility = View.GONE

        binding.addAnimeTextView.text = getString(R.string.add_anime_to_library)

        setAnimeAboutFragment()
        setAnimeEpisodesFragment()

        binding.animeTabLayout.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    showFragment(fragmentList[tab.position])
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            getTabAt(selectedTabPosition)?.apply {
                select()
                showFragment(fragmentList[position])
            }
        }
    }

    private fun setAnimeAboutFragment() {
        animeAboutList.apply {
            clear()
            add(anime.clone().apply { typeLayout = MangaJapAdapter.Type.ANIME_HEADER })
            add(anime.clone().apply { typeLayout = MangaJapAdapter.Type.ANIME_SUMMARY })
            if (anime.animeEntry != null)
                add(anime.clone().apply { typeLayout = MangaJapAdapter.Type.ANIME_PROGRESSION })
            add(anime.clone().apply { typeLayout = MangaJapAdapter.Type.ANIME_REVIEWS })
        }

        if (animeAboutFragment.isAdded) animeAboutFragment.mangaJapAdapter?.notifyDataSetChanged()
        addFragment(animeAboutFragment, getString(R.string.about))
    }

    private fun setAnimeEpisodesFragment() {
        anime.episodes.map {
            it.anime = anime
        }

        animeEpisodesList.apply {
            clear()
            if (anime.episodes.isNotEmpty())
                add(anime.episodes.last().clone().apply { typeLayout = MangaJapAdapter.Type.EPISODE_ANIME_HEADER })
        }
        for (season in anime.seasons) {
            animeEpisodesList.add(season)
            if (showSeason.contains(season.seasonNumber)) {
                for (episode in season.episodes) animeEpisodesList.add(episode.apply { typeLayout = MangaJapAdapter.Type.EPISODE_ANIME })
            }
        }

        if (animeEpisodesList.isNotEmpty()) {
            if (animeEpisodesFragment.isAdded) animeEpisodesFragment.mangaJapAdapter?.notifyDataSetChanged()
            addFragment(animeEpisodesFragment, getString(R.string.episodes))
        }
    }

    private fun addFragment(fragment: Fragment, title: String) {
        val ft: FragmentTransaction = childFragmentManager.beginTransaction()

        if (!fragmentList.contains(fragment)) {
            fragmentList.add(fragment)
            binding.animeTabLayout.addTab(binding.animeTabLayout.newTab().setText(title))
            if (!fragment.isAdded) {
                ft.add(binding.animeFrameLayout.id, fragment)
            }
        } else {
            if (!binding.animeTabLayout.contains(title)) {
                binding.animeTabLayout.addTab(binding.animeTabLayout.newTab().setText(title))
                if (fragment.isAdded) {
                    ft.detach(fragment)
                    ft.attach(fragment)
                }
            }
        }

        ft.commitAllowingStateLoss()
    }

    private fun showFragment(fragment: Fragment) {
        val ft = childFragmentManager.beginTransaction()
        for (i in fragmentList.indices) {
            if (fragmentList[i] === fragment) {
                ft.show(fragmentList[i])
            } else {
                ft.hide(fragmentList[i])
            }
        }
        ft.commitAllowingStateLoss()
    }

    private fun displayPopupWindow() {
        val layoutInflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupAnimeBinding = PopupAnimeBinding.inflate(layoutInflater)

        val popupWindow = PopupWindow(popupAnimeBinding.root, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true).apply {
            elevation = 25f
            showAtLocation(popupAnimeBinding.root, Gravity.TOP, 0, 140)
        }

        popupAnimeBinding.animeEntryStatusTextView.text = getString(anime.animeEntry!!.status.stringId)

        popupAnimeBinding.deleteAnime.setOnClickListener {
            viewModel.updateAnimeEntry(anime.animeEntry!!.apply {
                putAdd(false)
            })
            popupWindow.dismiss()
        }

        popupAnimeBinding.shareAnime.setOnClickListener { shareText(getString(R.string.shareAnime, anime.canonicalTitle)) }
    }


    companion object {
        const val ANIME_SYNOPSIS_MAX_LINES = 3
    }
}