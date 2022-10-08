package com.javilena87.fichaje.ui.result

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.javilena87.fichaje.data.prefs.FakeFichajeSharedPrefs
import com.javilena87.fichaje.data.repository.FakeFichajeRepository
import com.javilena87.fichaje.data.repository.FakeHolidayRepository
import com.javilena87.fichaje.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class ResultViewModelTest {

    private var fakeRepository: FakeFichajeRepository = FakeFichajeRepository()
    private var fakeExceptionRepository: FakeFichajeRepository = FakeFichajeRepository(exception = true)
    private var fakeErrorRepository: FakeFichajeRepository = FakeFichajeRepository(requestFailure = true)
    private var fakeHolidayRepository: FakeHolidayRepository = FakeHolidayRepository()

    private var fakeSharedPrefs: FakeFichajeSharedPrefs = FakeFichajeSharedPrefs()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    val testScheduler = TestCoroutineScheduler()

    @OptIn(ExperimentalCoroutinesApi::class)
    val testDispatcher = UnconfinedTestDispatcher(testScheduler)

    @ExperimentalCoroutinesApi
    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given success request when enter then return valid success data`() {
        val resultViewModel = ResultViewModel(fakeRepository, fakeHolidayRepository, fakeSharedPrefs)

        resultViewModel.enter()

        val value = resultViewModel.resultEnterState.getOrAwaitValue()

        assertTrue("Result is not true", value.requestOk)
    }

    @Test
    fun `given success request when enter then set last enter register`() {
        val resultViewModel = ResultViewModel(fakeRepository, fakeHolidayRepository, fakeSharedPrefs)

        resultViewModel.enter()

        assertEquals("Register is not valid", fakeSharedPrefs.getLastRegister(), "ENTRY")
    }

    @Test
    fun `given success request with not valid response when enter then return valid success data`() {
        val resultViewModel = ResultViewModel(fakeErrorRepository, fakeHolidayRepository, fakeSharedPrefs)

        resultViewModel.enter()

        val value = resultViewModel.resultEnterState.getOrAwaitValue()

        assertFalse("Result is not false", value.requestOk)
    }

    @Test
    fun `given fail request with exception when enter then return valid success data`() {
        val resultViewModel = ResultViewModel(fakeExceptionRepository, fakeHolidayRepository, fakeSharedPrefs)

        resultViewModel.enter()

        val value = resultViewModel.resultEnterState.getOrAwaitValue()

        assertFalse("Result is not false", value.requestOk)
    }
    @Test
    fun `given success request when exit then return valid success data`() {
        val resultViewModel = ResultViewModel(fakeRepository, fakeHolidayRepository, fakeSharedPrefs)

        resultViewModel.exit()

        val value = resultViewModel.resultExitState.getOrAwaitValue()

        assertTrue("Result is not true", value.requestOk)
    }

    @Test
    fun `given success request when exit then set last exit register`() {
        val resultViewModel = ResultViewModel(fakeRepository, fakeHolidayRepository, fakeSharedPrefs)

        resultViewModel.exit()

        assertEquals("Register is not valid", fakeSharedPrefs.getLastRegister(), "EXIT")
    }


    @Test
    fun `given success request with not valid response when exit then return valid success data`() {
        val resultViewModel = ResultViewModel(fakeErrorRepository, fakeHolidayRepository, fakeSharedPrefs)

        resultViewModel.exit()

        val value = resultViewModel.resultExitState.getOrAwaitValue()

        assertFalse("Result is not false", value.requestOk)
    }

    @Test
    fun `given fail request with exception when exit then return valid success data`() {
        val resultViewModel = ResultViewModel(fakeExceptionRepository, fakeHolidayRepository, fakeSharedPrefs)

        resultViewModel.exit()

        val value = resultViewModel.resultExitState.getOrAwaitValue()

        assertFalse("Result is not false", value.requestOk)
    }

    @Test
    fun `given a resultviewmodel when set alarm state true then the alarm state is stored with value true`() {
        val resultViewModel = ResultViewModel(fakeExceptionRepository, fakeHolidayRepository, fakeSharedPrefs)

        resultViewModel.setAlarm(true)

        val checkValue = resultViewModel.checkAlarm()
        val value = resultViewModel.resultAlarmOnOffState.getOrAwaitValue()

        assertTrue("Check alarm is not true", checkValue)
        assertTrue("Value is not true", value)
        assertTrue("State is not true", fakeSharedPrefs.getAlarmState())
    }

    @Test
    fun `given a resultviewmodel when set alarm state false then the alarm state is stored with value false`() {
        val resultViewModel = ResultViewModel(fakeExceptionRepository, fakeHolidayRepository, fakeSharedPrefs)

        resultViewModel.setAlarm(false)

        val value = resultViewModel.resultAlarmOnOffState.getOrAwaitValue()
        val checkValue = resultViewModel.checkAlarm()

        assertFalse("Value is not false", value)
        assertFalse("Check alarm is not true", checkValue)
        assertFalse("State is not false", fakeSharedPrefs.getAlarmState())
    }

    @Test
    fun `given a resultviewmodel when enable alarm then the alarm starts with milliseconds`() {
        val resultViewModel = ResultViewModel(fakeExceptionRepository, fakeHolidayRepository, fakeSharedPrefs)

        resultViewModel.enableAlarm()

        val value = resultViewModel.resultAlarmState.getOrAwaitValue()

        assertTrue("Alarm is not setted", value.nextAlarmDay > 0)
    }

}