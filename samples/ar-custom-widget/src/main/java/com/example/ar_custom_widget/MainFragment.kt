package com.example.ar_custom_widget

import android.Manifest
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.math.Position
import io.github.sceneview.node.VideoNode
import com.example.ar_custom_widget.utils.GeoPermissionHelper
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Anchor
import com.google.ar.core.Earth
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Rotation

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var sceneView: ArSceneView
    lateinit var videoNode: VideoNode
    lateinit var anchorNode: ArModelNode
    lateinit var earth: Earth
    private lateinit var actionButton: ExtendedFloatingActionButton


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GeoPermissionHelper.requestPermissions(fragment = this,
            onPermissionGranted = { createScenery(view) },
            onPermissionDenied = {
                Toast.makeText(requireContext(), "Permisos denegados", Toast.LENGTH_LONG).show()
            })
    }

    private fun createScenery(view: View) {
        sceneView = view.findViewById(R.id.sceneView)

        sceneView.apply {
            cloudAnchorEnabled = true
            geospatialEnabled = true

            onArSessionCreated = {
                earth = it.earth!!
            }

        }

        actionButton = view.findViewById(R.id.actionButton)
        actionButton.apply {
            setIconResource(R.drawable.ic_resolve)
            text = "Get Anchors"
            isVisible = true
        }
        actionButton.setOnClickListener {

            actionButton.apply {
                text = "Downloading..."
            }

            getAnchors()
        }


        sceneView.addChild(AugmentedImageNode(
            engine = sceneView.engine,
            imageName = "qrcode",
            bitmap = requireContext().assets.open("augmentedimages/augmented-images-earth.jpg")
                .use(BitmapFactory::decodeStream),
//            onUpdate = { node, _ ->
//                if (node.isTracking) {
//                    if (!videoNode.player.isPlaying) {
//                        videoNode.player.start()
//                    }
//                } else {
//                    if (videoNode.player.isPlaying) {
//                        videoNode.player.pause()
//                    }
//                }
//            }
        ).apply {
            videoNode = VideoNode(
                sceneView.engine,
                MediaPlayer().apply {
                    setDataSource(
                        requireContext(),
                        Uri.parse("https://vrestudio.com/storage/AppTurismo/videos/Gibralfaro.mp4")
                    )
                    isLooping = true
                    setOnPreparedListener {
                        if ((videoNode.parent as? AugmentedImageNode)?.isTracking == true) {
                            start()
                        }
                    }
                    onTap = { _, _ ->
                        if (!this.isPlaying) {
                            Log.d("message", "Is in play")
                            // Start the video
                            this.start()
                        } else {
                            Log.d("message", "Is in pause mode")
                            this.pause()
                        }

                    }
                    rotation = Rotation(x = 180.0f)
                    prepareAsync()
                },
                glbFileLocation = "https://vrestudio.com/storage/AppTurismo/objetos/16_9.glb",
                scaleToVideoRatio = false,
                centerOrigin = Position(y = 1.0f)
            )

            addChild(videoNode)
        })
    }

    private fun getAnchors() {

        val allPositions = mutableListOf<LatLng>()

        allPositions.add(LatLng(36.699564144711914, -4.439179610519211))
        allPositions.add(LatLng(36.69951575750139, -4.439319085388508))
        allPositions.add(LatLng(36.69939909043523, -4.439269464521546))
        allPositions.add(LatLng(36.699384036607334, -4.438987832573928))


        drawNodes(allPositions)


    }

    private fun drawNodes(allPositions: MutableList<LatLng>) {
        val cameraGeospatialPose = earth.cameraGeospatialPose
        var earthAnchor: Anchor? = null

        if (!earth.anchors?.isEmpty()!!) {
            for (anchor in earth.anchors) {
                anchor.detach()
                Log.d("message", "Im deleting anchor")
            }
        }
        for (pos in allPositions) {
            if (earth.trackingState == TrackingState.TRACKING) {
                Log.d("message", "Action button clicked")

                if (cameraGeospatialPose != null) {
                    earthAnchor?.detach()
                    earthAnchor = earth.createAnchor(
                        pos.latitude,
                        pos.longitude,
                        cameraGeospatialPose.altitude + 1.3,
                        0f,
                        0f,
                        0f,
                        1f
                    )
                }
                anchorNode = ArModelNode(sceneView.engine).apply {
                    placementMode = PlacementMode.BEST_AVAILABLE
                    // anchorPoseUpdateInterval = null
                    //isSmoothPoseEnable = true
                    //followHitPosition = false
                    applyPosePosition = true
                    isVisible = true
                    loadModelGlbAsync("https://vrestudio.com/storage/AppTurismo/objetos/map_pointer_3d_icon.glb")
                    anchor = earthAnchor
                    isScaleEditable = false
                    minEditableScale = 10.0f
                }
                sceneView.addChild(anchorNode)
            }
            actionButton.apply {
                text = "Done"
            }
        }


    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        GeoPermissionHelper.handlePermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        videoNode.player.stop()
    }


}