package io.github.sceneview.renderable

import androidx.annotation.IntRange
import androidx.lifecycle.Lifecycle
import com.google.android.filament.EntityInstance
import com.google.android.filament.MaterialInstance
import com.google.android.filament.RenderableManager
import com.gorisse.thomas.lifecycle.observe
import io.github.sceneview.Filament
import io.github.sceneview.light.destroy

typealias Renderable = Int
typealias RenderableInstance = Int

const val RENDER_PRIORITY_DEFAULT = 4
const val RENDER_PRIORITY_FIRST = 0
const val RENDER_PRIORITY_LAST = 7

/**
 * @see RenderableManager.getInstance
 */
val Renderable.renderableInstance: RenderableInstance
    @EntityInstance get() = Filament.renderableManager.getInstance(this)

fun RenderableManager.Builder.build(lifecycle: Lifecycle): Renderable =
    Filament.entityManager.create().apply {
        build(Filament.engine, this)
    }.also { renderable ->
        lifecycle.observe(onDestroy = {
            // Prevent double destroy in case of manually destroyed
            runCatching { renderable.destroy() }
        })
    }

/**
 * @see RenderableManager.setPriority
 */
fun Renderable.setPriority(@IntRange(from = 0, to = 7) priority: Int) =
    Filament.renderableManager.setPriority(renderableInstance, priority)

/**
 * @see RenderableManager.getMaterialInstanceAt
 */
fun Renderable.getMaterial(@IntRange(from = 0) primitiveIndex: Int = 0) =
    Filament.renderableManager.getMaterialInstanceAt(renderableInstance, primitiveIndex)

/**
 * @see RenderableManager.setMaterialInstanceAt
 */
fun Renderable.setMaterial(
    material: MaterialInstance,
    @IntRange(from = 0) primitiveIndex: Int = 0
) = Filament.renderableManager.setMaterialInstanceAt(renderableInstance, primitiveIndex, material)

/**
 * @see RenderableManager.setCastShadows
 */
fun Renderable.setCastShadows(enabled: Boolean) =
    Filament.renderableManager.setCastShadows(renderableInstance, enabled)

/**
 * @see RenderableManager.setReceiveShadows
 */
fun Renderable.setReceiveShadows(enabled: Boolean) =
    Filament.renderableManager.setReceiveShadows(renderableInstance, enabled)

/**
 * @see RenderableManager.setCulling
 */
fun Renderable.setCulling(enabled: Boolean) =
    Filament.renderableManager.setCulling(renderableInstance, enabled)

/**
 * @see RenderableManager.setBlendOrder
 */
fun Renderable.setBlendOrder(
    @IntRange(from = 0, to = 65535) blendOrder: Int,
    @IntRange(from = 0) primitiveIndex: Int = 0
) = Filament.renderableManager.setBlendOrderAt(renderableInstance, primitiveIndex, blendOrder)

/**
 * @see RenderableManager.setGlobalBlendOrderEnabledAt
 */
fun Renderable.setGlobalBlendOrderEnabled(
    enabled: Boolean,
    @IntRange(from = 0) primitiveIndex: Int = 0,
) = Filament.renderableManager.setGlobalBlendOrderEnabledAt(
    renderableInstance,
    primitiveIndex,
    enabled
)

/**
 * @see RenderableManager.setScreenSpaceContactShadows
 */
fun Renderable.setScreenSpaceContactShadows(enabled: Boolean) =
    Filament.renderableManager.setScreenSpaceContactShadows(renderableInstance, enabled)
