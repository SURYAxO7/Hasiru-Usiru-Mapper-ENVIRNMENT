package com.hasiru.usiru.mapper

import com.hasiru.usiru.mapper.domain.engine.OxygenScoreEngine
import org.junit.Assert.assertEquals
import org.junit.Test

class OxygenScoreEngineTest {

    private val engine = OxygenScoreEngine()

    @Test
    fun `neem oxygen score equals girth times factor`() {
        val score = engine.calculateOxygenScore(50.0, "Neem")
        assertEquals(75.0, score, 0.01)
    }

    @Test
    fun `banyan has highest factor`() {
        val banyan = engine.speciesFactor("Banyan")
        val coconut = engine.speciesFactor("Coconut")
        assert(banyan > coconut)
    }

    @Test
    fun `zero girth returns zero score`() {
        assertEquals(0.0, engine.calculateOxygenScore(0.0, "Mango"), 0.01)
    }
}
