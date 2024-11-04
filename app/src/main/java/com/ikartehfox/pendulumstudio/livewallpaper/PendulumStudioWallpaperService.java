package com.ikartehfox.pendulumstudio.livewallpaper;

import android.opengl.GLSurfaceView.Renderer;

import android.preference.PreferenceManager;

public class PendulumStudioWallpaperService extends OpenGLES2WallpaperService {
    private String currentPendulum;

    @Override
    Renderer getNewRenderer() {
        currentPendulum = PreferenceManager.getDefaultSharedPreferences(
                this).getString("pendulum_selection", "5");
//        Log.d("getNewRenderer()", currentPendulum);
        switch (currentPendulum) {
            case "0":
                return new PendulumRenderer(PendulumRenderer.mPendulumMP);
            case "1":
                return new PendulumRenderer(PendulumRenderer.mPendulumSP);
            case "2":
                return new PendulumRenderer(PendulumRenderer.mPendulumSP2D);
            case "3":
                return new PendulumRenderer(PendulumRenderer.mPendulumSP3D);
            case "4":
                return new PendulumRenderer(PendulumRenderer.mPendulumDP);
            case "5":
                return new PendulumRenderer(PendulumRenderer.mPendulumDSP);
            case "6":
                return new PendulumRenderer(PendulumRenderer.mPendulumSMP);
            case "7":
                return new PendulumRenderer(PendulumRenderer.mPendulumSSP);
            default:
                return new PendulumRenderer(PendulumRenderer.mPendulumPW);
        }
    }

    @Override
    boolean needNewPendulum() {
        String pendulum = PreferenceManager.getDefaultSharedPreferences(
                this).getString("pendulum_selection", "5");
        return !pendulum.equals(currentPendulum);
    }
}
