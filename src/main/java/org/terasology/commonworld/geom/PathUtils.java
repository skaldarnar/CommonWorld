/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.commonworld.geom;

import java.awt.geom.Path2D;
import java.util.List;

import org.terasology.math.Vector2i;

/**
 * Some spline-related utilities
 * @author Martin Steiger
 */
public final class PathUtils {
    
    private PathUtils() {
        // private
    }
    
    /**
     * @param points a list of points
     * @return a path of connected line segments
     */
    public static Path2D createSegmentPath(List<Vector2i> points) {
        Path2D path = new Path2D.Double();

        path.moveTo(points.get(0).getX(), points.get(0).getY());

        for (int i = 0; i < points.size() - 1; i++) {
            Vector2i p1 = points.get(i + 1);
            path.lineTo(p1.getX(), p1.getY());
        }

        return path;

    }

    /**
     * Create a bezier spline that passes through all given points
     * @param pts a list of points
     * @param smoothness the smoothness of the path
     * @return a path of connected curve segments
     */
    public static Path2D getBezierSplinePath(List<Vector2i> pts, double smoothness) {
        Path2D path = new Path2D.Double();

        if (pts.isEmpty()) {
            return path;
        }
        
        path.moveTo(pts.get(0).x, pts.get(0).y);

        for (int i = 0; i < pts.size() - 1; i++) {
            Vector2i p1 = pts.get(i + 1);
            Vector2i c0 = computeNextControlPoint(pts, i, smoothness);
            Vector2i c1 = computePreviousControlPoint(pts, i + 1, smoothness);
            path.curveTo(c0.x, c0.y, c1.x, c1.y, p1.x, p1.y);
        }

        return path;
    }
 
    private static Vector2i add(Vector2i a, Vector2i b) {
        return new Vector2i(a.x + b.x, a.y + b.y);
    }
    private static Vector2i sub(Vector2i a, Vector2i b) {
        return new Vector2i(a.x - b.x, a.y - b.y);
    }

    private static Vector2i mul(Vector2i p, double f) {
        return new Vector2i((int) (p.x * f), (int) (p.y * f));
    }

    private static Vector2i computePreviousControlPoint(List<Vector2i> points, int index, double smoothness) {
        if (index == points.size() - 1) {
            Vector2i p0 = points.get(index - 1);
            Vector2i p1 = points.get(index);
            Vector2i p0to1 = sub(p1, p0);
            Vector2i c = sub(p1, mul(p0to1, smoothness));
            return c;
        }
        Vector2i p0 = points.get(index - 1);
        Vector2i p1 = points.get(index);
        Vector2i p2 = points.get(index + 1);
        Vector2i p0to2 = sub(p2, p0);
        Vector2i c = sub(p1, mul(p0to2, smoothness));
        return c;
    }

    private static Vector2i computeNextControlPoint(List<Vector2i> points, int index, double smoothness) {
        if (index == 0) {
            Vector2i p0 = points.get(index);
            Vector2i p1 = points.get(index + 1);
            Vector2i p0to1 = sub(p1, p0);
            Vector2i c = add(p0, mul(p0to1, smoothness));
            return c;
        }
        Vector2i p0 = points.get(index - 1);
        Vector2i p1 = points.get(index);
        Vector2i p2 = points.get(index + 1);
        Vector2i p0to2 = sub(p2, p0);
        Vector2i c = add(p1, mul(p0to2, smoothness));
        return c;
    }
}
