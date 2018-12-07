package com.kedzie.vbox.fest;

import org.fest.assertions.api.android.view.AbstractViewAssert;

import com.kedzie.vbox.app.CollapsiblePanelView;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * FEST asserts for CollapsiblePanelView
 */
public class PanelAssert extends AbstractViewAssert<PanelAssert, CollapsiblePanelView> {
    
    public PanelAssert(CollapsiblePanelView actual) {
        super(actual, PanelAssert.class);
      }

      public PanelAssert isExpanded() {
        isNotNull();
        assertThat(actual.isExpanded())
            .isEqualTo(true)
            .overridingErrorMessage("Panel expected to be Expanded");
        return this;
      }
      
      public PanelAssert isCollapsed() {
          isNotNull();
          assertThat(actual.isExpanded())
              .isEqualTo(false)
              .overridingErrorMessage("Panel expected to be Collapsed");
          return this;
        }
    }