package movielistsample.movies

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import com.yasintanriverdi.commons.extensions.appContext
import com.yasintanriverdi.commons.extensions.observe
import com.yasintanriverdi.commons.extensions.showSnackbar
import com.yasintanriverdi.commons.ui.GridViewItemDecoration
import com.yasintanriverdi.core.data.DataState
import com.yasintanriverdi.core.data.entities.Movie
import com.yasintanriverdi.core.di.provider.CoreComponentProvider
import com.yasintanriverdi.movies.R
import com.yasintanriverdi.movies.databinding.MoviesFragmentMoviesBinding
import movielistsample.movies.adapter.MoviesAdapter
import movielistsample.movies.di.DaggerMoviesComponent
import movielistsample.movies.di.MoviesModule
import javax.inject.Inject

class MoviesFragment : Fragment() {

    @Inject
    lateinit var viewModel: MoviesViewModel

    private lateinit var binding: MoviesFragmentMoviesBinding

    private lateinit var moviesAdapter: MoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MoviesFragmentMoviesBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setupDependencyInjection()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observe(viewModel.state, ::onViewStateChanged)
        observe(viewModel.data, ::onViewDataChanged)
        observe(viewModel.event, ::onViewEvent)
    }

    private fun setupRecyclerView() {
        moviesAdapter = MoviesAdapter(viewModel)
        with(binding.moviesList) {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(GridViewItemDecoration(
                    spanCount = resources.getInteger(R.integer.movie_list_span_count),
                    spacing = resources.getDimensionPixelOffset(R.dimen.movie_item_spacing)
                )
            )
            adapter = moviesAdapter
        }
    }

    private fun onViewStateChanged(viewState: MoviesViewState) {
        binding.viewState = viewState
        when (val dataState = viewState.dataState) {
            is DataState.Error -> {
                showSnackbar(dataState.message)
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun onViewDataChanged(movies: PagedList<Movie>) {
        moviesAdapter.submitList(movies)
    }

    private fun onViewEvent(viewEvent: MoviesEvent) {
        when (viewEvent) {
            is MoviesEvent.OpenMovieDetail -> {
                findNavController().navigate(
                    MoviesFragmentDirections.actionMoviesFragmentToMovieDetailFragment(
                        viewEvent.movieId, viewEvent.movieTitle)
                )
            }
        }
    }

    private fun setupDependencyInjection() {
        DaggerMoviesComponent
            .builder()
            .coreComponent((appContext as CoreComponentProvider).provideCoreComponent())
            .moviesModule(MoviesModule(this))
            .build()
            .inject(this)
    }
}