/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


internal fun NoaFloat.testingCopying(device: Device = Device.CPU): Unit {
    val array = (1..24).map { 10f * it * it }.toFloatArray()
    val shape = intArrayOf(2, 3, 4)
    val tensor = copyFromArray(array, shape = shape, device = device)
    val copyOfTensor = tensor.copy()
    tensor[intArrayOf(1, 2, 3)] = 0.1f
    assertTrue(copyOfTensor.copyToArray() contentEquals array)
    assertEquals(0.1f, tensor[intArrayOf(1, 2, 3)])
    if (device != Device.CPU) {
        val normalCpu = randNormal(intArrayOf(2, 3))
        val normalGpu = normalCpu.copyToDevice(device)
        assertTrue(normalCpu.copyToArray() contentEquals normalGpu.copyToArray())

        val uniformGpu = randUniform(intArrayOf(3, 2), device)
        val uniformCpu = uniformGpu.copyToDevice(Device.CPU)
        assertTrue(uniformGpu.copyToArray() contentEquals uniformCpu.copyToArray())
    }
}

internal fun NoaInt.testingViewWithNoCopy(device: Device = Device.CPU) {
    val tensor = copyFromArray(intArrayOf(1, 2, 3, 4, 5, 6), shape = intArrayOf(6), device)
    val viewTensor = tensor.view(intArrayOf(2, 3))
    assertTrue(viewTensor.shape contentEquals intArrayOf(2, 3))
    viewTensor[intArrayOf(0, 0)] = 10
    assertEquals(tensor[intArrayOf(0)], 10)
}

class TestTensor {
    @Test
    fun testCopying() = NoaFloat {
        withCuda { device ->
            testingCopying(device)
        }
    }!!

    @Test
    fun testRequiresGrad() = NoaFloat {
        val tensor = randNormal(intArrayOf(3))
        assertTrue(!tensor.requiresGrad)
        tensor.requiresGrad = true
        assertTrue(tensor.requiresGrad)
        tensor.requiresGrad = false
        assertTrue(!tensor.requiresGrad)
        tensor.requiresGrad = true
        val detachedTensor = tensor.detachFromGraph()
        assertTrue(!detachedTensor.requiresGrad)
    }!!

    @Test
    fun testTypeMoving() = NoaFloat {
        val tensorInt = copyFromArray(floatArrayOf(1f, 2f, 3f), intArrayOf(3)).asInt()
        NoaInt {
            val temporalTensor = copyFromArray(intArrayOf(4, 5, 6), intArrayOf(3))
            tensorInt swap temporalTensor
            assertTrue(temporalTensor.copyToArray() contentEquals intArrayOf(1, 2, 3))
        }
        assertTrue(tensorInt.asFloat().copyToArray() contentEquals floatArrayOf(4f, 5f, 6f))
    }!!

    @Test
    fun testViewWithNoCopy() = NoaInt {
        withCuda { device ->
            testingViewWithNoCopy(device)
        }
    }!!

}