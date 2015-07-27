/*
* Copyright (C) 2015 The CyanogenMod Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.android.ex.chips;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Subclass of TextView which displays a snippet of text which matches the full text and
 * highlights the matches within the snippet.
 */
public class TextViewSnippet extends TextView {
    private static final String ELLIPSIS = "\u2026";

    private String mFullText;
    private String mTargetString;
    private Pattern mPattern;

    public TextViewSnippet(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewSnippet(Context context) {
        super(context);
    }

    public TextViewSnippet(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * We have to know our width before we can compute the snippet string.  Do that
     * here and then defer to super for whatever work is normally done.
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        String fullTextLower = mFullText.toLowerCase();
        String targetStringLower = mTargetString.toLowerCase();

        int startPos = 0;
        int searchStringLength = targetStringLower.length();
        int bodyLength = fullTextLower.length();

        Matcher m = mPattern.matcher(mFullText);
        if (m.find(0)) {
            startPos = m.start();
        }
        TextPaint tp = getPaint();

        float searchStringWidth = tp.measureText(mTargetString);
        float textFieldWidth = getWidth();

        float ellipsisWidth = tp.measureText(ELLIPSIS);
        textFieldWidth -= ellipsisWidth;

        String snippetString = null;
        if (searchStringWidth > textFieldWidth) {
            snippetString = mFullText.substring(startPos, startPos + searchStringLength);
        } else {

            int offset = -1;
            int start = -1;
            int end = -1;
            /* TODO: this code could be made more efficient by only measuring the additional
             * characters as we widen the string rather than measuring the whole new
             * string each time.
             */
            while (true) {
                offset += 1;

                int newstart = Math.max(0, startPos - offset);
                int newend = Math.min(bodyLength, startPos + searchStringLength + offset);

                if (newstart == start && newend == end) {
                    // if we couldn't expand out any further then we're done
                    break;
                }
                start = newstart;
                end = newend;

                // pull the candidate string out of the full text rather than body
                // because body has been toLower()'ed
                String candidate = mFullText.substring(start, end);
                if (tp.measureText(candidate) > textFieldWidth) {
                    // if the newly computed width would exceed our bounds then we're done
                    // do not use this "candidate"
                    break;
                }

                snippetString = String.format(
                        "%s%s%s",
                        start == 0 ? "" : ELLIPSIS,
                        candidate,
                        end == bodyLength ? "" : ELLIPSIS);
            }
        }

        SpannableString spannable = new SpannableString(snippetString);
        int start = 0;

        m = mPattern.matcher(snippetString);
        while (m.find(start)) {
            spannable.setSpan(new StyleSpan(Typeface.BOLD), m.start(), m.end(), 0);
            start = m.end();
        }
        setText(spannable);

        // do this after the call to setText() above
        super.onLayout(changed, left, top, right, bottom);
    }

    public void setText(String fullText, String target) {
        target = (target == null) ? "" : target;
        mPattern = Pattern.compile(Pattern.quote(target), Pattern.CASE_INSENSITIVE);

        mFullText = fullText;
        mTargetString = target;
        requestLayout();
    }
}

