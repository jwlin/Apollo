(function ($) {

Drupal.behaviors.filetree = {

  attach: function(context, settings) {

    // Collapse the sub-folders.
    $('.filetree .files ul').hide();

    // Expand/collapse sub-folder when clicking parent folder.
    $('.filetree .files li.folder').click(function(e) {
      // A link was clicked, so don't mess with the folders.
      if ($(e.target).is('a')) {
        return;
      }
      // Determine whether or not to use an animation when toggling folders.
      var animation = $(this).parents('.filetree').hasClass('filetree-animation') ? 'fast' : '';
      // If multiple folders are not allowed, collapse non-parent folders.
      if (!$(this).parents('.filetree').hasClass('multi')) {
        $(this).parents('.files').find('li.folder').not($(this).parents()).not($(this)).removeClass('expanded').find('ul:first').hide(animation);
      }
      // Expand.
      if (!$(this).hasClass('expanded')) {
        $(this).addClass('expanded').find('ul:first').show(animation);
      }
      // Collapse.
      else {
        $(this).removeClass('expanded').find('ul:first').hide(animation);
      }
      // Prevent collapsing parent folders.
      return false;
    });

    // Expand/collapse all when clicking controls.
    $('.filetree .controls a').click(function() {
      var animation = $(this).parents('.filetree').hasClass('filetree-animation') ? 'fast' : '';
      if ($(this).hasClass('expand')) {
        $(this).parents('.filetree').find('.files li.folder').addClass('expanded').find('ul:first').show(animation);
      }
      else {
        $(this).parents('.filetree').find('.files li.folder').removeClass('expanded').find('ul:first').hide(animation);
      }
      return false;
    });

  }

};

})(jQuery);
;
(function ($) {
  Drupal.behaviors.triapl_analysis_blast = {
    attach: function (context, settings) {

      // hide all the HSP description boxes by default
      $(".tripal-analysis-blast-info-hsp-desc").hide();

      // when the [more] link is clicked, show the appropriate HSP description box
      $(".tripal-analysis-blast-info-hsp-link").click(function(e) {
        var my_id = e.target.id;
        var re = /hsp-link-(\d+)-(\d+)/;
        var matches = my_id.match(re);
        var analysis_id = matches[1];
        var j = matches[2];
        $(".tripal-analysis-blast-info-hsp-desc").hide();
        $("#hsp-desc-" + analysis_id + "-" +j).show();
      });

      // when the [Close] button is clicked on the HSP description close the box
      $(".hsp-desc-close").click(function(e) {
        $(".tripal-analysis-blast-info-hsp-desc").hide();
      });

      // add the achor to the pager links so that when the user clicks a pager
      // link and the page refreshes they are taken back to the location
      // on the page that they were viewing
      $("div.tripal_analysis_blast-info-box-desc ul.pager a").each(function() {
        pager_link = $(this);
        parent = pager_link.parents('div.tripal_analysis_blast-info-box-desc');
        pager_link.attr('href', pager_link.attr('href') + '#' + parent.attr('id')); 
      })
    }
  };

})(jQuery);;
if (Drupal.jsEnabled) {

   function tripal_get_base_url() {

       // Get the base url. Drupal can not pass it through a form so we need 
       // to get it ourself. Use different patterns to match the url in case
       // the Clean URL function is turned on
       var baseurl = location.href.substring(0,location.href.lastIndexOf('/?q=/node'));
       if(!baseurl) {
         var baseurl = location.href.substring(0,location.href.lastIndexOf('/node'));
       }
       if (!baseurl) {
         // This base_url is obtained when Clena URL function is off
         var baseurl = location.href.substring(0,location.href.lastIndexOf('/?q=node'));
       }
       if (!baseurl) {
         // The last possibility is we've assigned an alias path, get base_url til the last /
         var baseurl = location.href.substring(0,location.href.firstIndexOf('/'));
       }
       return baseurl;
   }  
};
(function($) {
  Drupal.behaviors.tripal_featureBehavior = {
    attach: function (context, settings){

      // the following function is used when viewing sequence alignments
      // on the feature page. When mousing over the feature type legend
      // it should highlight regions in the sequence
      $('.tripal_feature-legend-item').bind("mouseover", function(){
        var classes = $(this).attr('class').split(" ");
        var type_class = classes[1];       

        $("." + type_class).css("border", "1px solid red");
        $(this).bind("mouseout", function(){
          $("." + type_class).css("border", "0px");
        })    
      });
    }
  };
})(jQuery);;
