/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package colin.com.module.camera.photo.base;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A collection class that automatically groups {@link Size}s by their {@link AspectRatio}s.
 */
public class SizeMap {


    private SortedSet<Size> sizesCompare = new TreeSet<>();


    public void compareRatio(AspectRatio aspectRatio, Size size) {
        if (aspectRatio == null) {
            return;
        }
        if (aspectRatio.matches(size)) {
            sizesCompare.add(size);
        }

    }



    public SortedSet<Size> getSizesCompare() {
        return sizesCompare;
    }


    public SortedSet<Size> getCompareSize(AspectRatio ratio) {
        return sizesCompare;
    }

    public void clear() {
        sizesCompare.clear();
    }

    boolean isEmpty() {
        return sizesCompare.isEmpty();
    }

}
