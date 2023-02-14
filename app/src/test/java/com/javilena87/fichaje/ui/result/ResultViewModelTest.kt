package com.javilena87.fichaje.ui.result

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.javilena87.fichaje.data.prefs.FakeFichajeSharedPrefs
import com.javilena87.fichaje.data.repository.FakeFichajeRepository
import com.javilena87.fichaje.data.repository.FakeHolidayRepository
import com.javilena87.fichaje.domain.usecases.alarm.*
import com.javilena87.fichaje.getOrAwaitValue
import com.javilena87.fichaje.presentation.result.ResultViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
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

    private val setInitTimeUseCase = SetInitTimeUseCase(fakeSharedPrefs)

    private val getAlarmInitTimeUseCase = GetAlarmInitTimeUseCase(fakeHolidayRepository)

    private val getAlarmStateUseCase = GetAlarmStateUseCase(fakeSharedPrefs)

    private val setAlarmStateUseCase = SetAlarmStateUseCase(fakeSharedPrefs)

    private val resultViewModelHappyPath = ResultViewModel(
        SetEnterUseCase(fakeRepository, fakeSharedPrefs),
        SetExitUseCase(fakeRepository, fakeSharedPrefs),
        setInitTimeUseCase,
        getAlarmInitTimeUseCase,
        getAlarmStateUseCase,
        setAlarmStateUseCase)

    private val resultViewModelException = ResultViewModel(
        SetEnterUseCase(fakeExceptionRepository, fakeSharedPrefs),
        SetExitUseCase(fakeExceptionRepository, fakeSharedPrefs),
        setInitTimeUseCase,
        getAlarmInitTimeUseCase,
        getAlarmStateUseCase,
        setAlarmStateUseCase)

    private val resultViewModelError = ResultViewModel(
        SetEnterUseCase(fakeErrorRepository, fakeSharedPrefs),
        SetExitUseCase(fakeErrorRepository, fakeSharedPrefs),
        setInitTimeUseCase,
        getAlarmInitTimeUseCase,
        getAlarmStateUseCase,
        setAlarmStateUseCase)

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
        resultViewModelHappyPath.enter()

        val value = resultViewModelHappyPath.resultEnterState.getOrAwaitValue()

        assertTrue("Result is not true", value.requestOk)
    }

    @Test
    fun `given success request when enter then set last enter register`() {
        resultViewModelHappyPath.enter()

        assertEquals("Register is not valid", fakeSharedPrefs.getLastRegister(), "ENTRY")
    }

    @Test
    fun `given success request with not valid response when enter then return valid success data`() {
        resultViewModelError.enter()

        val value = resultViewModelError.resultEnterState.getOrAwaitValue()

        assertFalse("Result is not false", value.requestOk)
    }

    @Test
    fun `given fail request with exception when enter then return valid success data`() {
        resultViewModelException.enter()

        val value = resultViewModelException.resultEnterState.getOrAwaitValue()

        assertFalse("Result is not false", value.requestOk)
    }
    @Test
    fun `given success request when exit then return valid success data`() {
        resultViewModelHappyPath.exit()

        val value = resultViewModelHappyPath.resultExitState.getOrAwaitValue()

        assertTrue("Result is not true", value.requestOk)
    }

    @Test
    fun `given success request when exit then set last exit register`() {
        resultViewModelHappyPath.exit()

        assertEquals("Register is not valid", fakeSharedPrefs.getLastRegister(), "EXIT")
    }


    @Test
    fun `given success request with not valid response when exit then return valid success data`() {
        resultViewModelError.exit()

        val value = resultViewModelError.resultExitState.getOrAwaitValue()

        assertFalse("Result is not false", value.requestOk)
    }

    @Test
    fun `given fail request with exception when exit then return valid success data`() {
        resultViewModelException.exit()

        val value = resultViewModelException.resultExitState.getOrAwaitValue()

        assertFalse("Result is not false", value.requestOk)
    }

    @Test
    fun `given a resultviewmodel when set alarm state true then the alarm state is stored with value true`() {
        resultViewModelException.setAlarm(true)

        val checkValue = resultViewModelException.checkAlarm()
        val value = resultViewModelException.resultAlarmOnOffState.getOrAwaitValue()

        assertTrue("Check alarm is not true", checkValue)
        assertTrue("Value is not true", value)
        assertTrue("State is not true", fakeSharedPrefs.getAlarmState())
    }

    @Test
    fun `given a resultviewmodel when set alarm state false then the alarm state is stored with value false`() {
        resultViewModelException.setAlarm(false)

        val value = resultViewModelException.resultAlarmOnOffState.getOrAwaitValue()
        val checkValue = resultViewModelException.checkAlarm()

        assertFalse("Value is not false", value)
        assertFalse("Check alarm is not true", checkValue)
        assertFalse("State is not false", fakeSharedPrefs.getAlarmState())
    }

    @Test
    fun `given a resultViewModel when enable alarm then the alarm starts with milliseconds`() {
        resultViewModelException.enableAlarm()

        val value = resultViewModelException.resultAlarmState.getOrAwaitValue()

        assertTrue("Alarm is not setted", value.nextAlarmDay > 0)
    }

}