package com.yelp.codegen.samples.kotlincoroutines.tools

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.ExternalResource

/**
 *  JUnit rule to create a Retrofit instance able to interact with the [MockWebServer] and to create Retrofit instances
 *  on the fly as needed.
 */
@ExperimentalCoroutinesApi
class CoroutineDispatcherRule : ExternalResource() {

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

    override fun before() {
        super.before()
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    override fun after() {
        Dispatchers.resetMain()
        testCoroutineScope.cleanupTestCoroutines()
        super.after()
    }

    fun runBlockingTest(block: suspend CoroutineScope.() -> Unit) =
        runBlocking(Dispatchers.Main, block)
}
