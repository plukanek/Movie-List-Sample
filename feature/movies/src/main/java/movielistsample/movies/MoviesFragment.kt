package movielistsample.movies

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.yasintanriverdi.commons.extensions.appContext
import com.yasintanriverdi.commons.extensions.observe
import com.yasintanriverdi.commons.ui.GridViewItemDecoration
import com.yasintanriverdi.core.data.entities.Movie
import com.yasintanriverdi.core.di.provider.CoreComponentProvider
import com.yasintanriverdi.movies.R
import com.yasintanriverdi.movies.databinding.FragmentMoviesBinding
import movielistsample.movies.adapter.MoviesAdapter
import movielistsample.movies.di.DaggerMoviesComponent
import movielistsample.movies.di.MoviesModule
import javax.inject.Inject

class MoviesFragment : Fragment(R.layout.fragment_movies) {

    @Inject
    lateinit var viewModel: MoviesViewModel

    lateinit var binding: FragmentMoviesBinding

    private val adapter by lazy {
        MoviesAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoviesBinding.inflate(inflater)
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
    }

    private fun setupRecyclerView() {
        binding.moviesList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.moviesList.addItemDecoration(
            GridViewItemDecoration(
                spanCount = resources.getInteger(R.integer.movie_list_span_count),
                spacing = resources.getDimensionPixelOffset(R.dimen.movie_item_spacing)
            )
        )
        binding.moviesList.adapter = adapter
    }

    private fun onViewStateChanged(viewState: MoviesViewState) {
        binding.viewState = viewState
    }

    private fun onViewDataChanged(movies: List<Movie>) {
        adapter.movies.addAll(movies)
        adapter.notifyDataSetChanged()
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