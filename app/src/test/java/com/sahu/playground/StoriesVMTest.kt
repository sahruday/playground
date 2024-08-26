package com.sahu.playground

import com.sahu.playground.data.Repository
import com.sahu.playground.data.remte.RemoteService
import com.sahu.playground.data.remte.api.Api
import com.sahu.playground.stories.StoriesResponse
import com.sahu.playground.stories.StoriesVM
import com.sahu.playground.stories.Story
import com.sahu.playground.stories.UserStory
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class StoriesVMTest{

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var api: Api

    private lateinit var repo: Repository
    private lateinit var viewModel: StoriesVM

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        repo = Repository(RemoteService(api))
        viewModel = StoriesVM(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loading`() = runTest {
        viewModel.getStories()

        val output = viewModel.state.first()
        assertEquals(true, output is StoriesVM.LOADING)
    }

    @Test
    fun `load success data`() = runTest {

        val storiesResponse =
            StoriesResponse(200, "", listOf(UserStory("1", "User1", listOf(Story("101", "")))))

        Mockito.`when`(repo.getData()).thenReturn(storiesResponse)

        viewModel.getStories()

        val output = viewModel.state.drop(1).first()
        assertEquals(true, output is StoriesVM.SUCCESS)
        assertEquals(storiesResponse.data, (output as StoriesVM.SUCCESS).stories)
    }


    @Test
    fun `load success data with error code`() = runTest {
        val storiesResponse =
            StoriesResponse(404, "Something went wrong", listOf())

        Mockito.`when`(repo.getData()).thenReturn(storiesResponse)

        viewModel.getStories()

        val output = viewModel.state.drop(1).first()
        assertEquals(true, output is StoriesVM.ERROR)
        assertEquals(storiesResponse.message, (output as StoriesVM.ERROR).message)
    }

    @Test
    fun `error while loading`() = runTest {

        Mockito.`when`(repo.getData()).thenThrow(IllegalStateException())

        viewModel.getStories()

        val output = viewModel.state.drop(1).first()
        assertEquals(true, output is StoriesVM.ERROR)
        assertEquals("Something went wrong", (output as StoriesVM.ERROR).message)
    }
}