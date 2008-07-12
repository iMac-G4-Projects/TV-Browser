package movieawardplugin;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

import java.util.logging.Logger;
import java.util.ArrayList;


public class MovieAwardHandler extends DefaultHandler {
  private static Logger mLog = Logger.getLogger(MovieAwardHandler.class.getName());

  /**
   * Holds the text of the current tag.
   */
  private StringBuffer mText;
  private Attributes mAttributes;

  /**
   * Name of the Parent-Node
   */
  private ArrayList<String> mNodeNames = new ArrayList<String>();
  /**
   * The current movie award to fill with data
   */
  private MovieAward mAward;
  private MovieAwardCategorie mCategorie;
  private Movie mMovie;

  public MovieAwardHandler(MovieAward movieAward) {
    mAward = movieAward;
    mText = new StringBuffer();
  }

  /**
   * Handles the occurence of tag text.
   */
  @Override
  public void characters(char ch[], int start, int length)
      throws SAXException {
    // There is some text -> Add it to the text buffer
    mText.append(ch, start, length);
  }

  /**
   * Handles the occurence of a start tag.
   */
  @Override
  public void startElement(String uri, String localName, String qName,
                           Attributes attributes)
      throws SAXException {

    mNodeNames.add(qName);
    // A new tag begins -> Clear the text buffer
    clear(mText);

    mAttributes = attributes;

    if ("categorie".equals(qName)) {
      mCategorie = new MovieAwardCategorie(attributes.getValue("id"));
    } else if ("movie".equals(qName)) {
      mMovie = new Movie(attributes.getValue("id"));

      int year = -1;
      try {
        year = Integer.parseInt(attributes.getValue("year"));
      } catch (NumberFormatException ex) {
        year = -1;
      }

      mMovie.setProductionYear(year);
      mMovie.setDirector(attributes.getValue("director"));
    } else if ("award".equals(qName)) {
      int year = -1;
      try {
        year = Integer.parseInt(attributes.getValue("year"));
      } catch (NumberFormatException ex) {
        year = -1;
      }

      mAward.addAward(new Award(attributes.getValue("categorie"),
                                attributes.getValue("status"),
                                attributes.getValue("movie"),
                                year
          ));
    }

  }

  /**
   * Handles the occurence of an end tag.
   */
  @Override
  public void endElement(String uri, String localName, String qName)
      throws SAXException {
    String parent = null;
    if (mNodeNames.size() > 1) {
      parent = mNodeNames.get(mNodeNames.size() - 2);
    }

    if ("name".equals(qName)) {
      if ("awarddata".equals(parent)) {
        mAward.addName(mAttributes.getValue("lang"), mText.toString());
      } else if ("categorie".equals(parent)) {
        mCategorie.addName(mAttributes.getValue("lang"), mText.toString());
      }
    } else if ("categorie".equals(qName)) {
      mAward.addCategorie(mCategorie);
    } else if ("title".equals(qName) && "movie".equals(parent)) {
      mMovie.addTitle(mAttributes.getValue("lang"), mText.toString());
    } else if ("movie".equals(qName)) {
      mAward.addMovie(mMovie);
    }

    mNodeNames.remove(mNodeNames.size() -1);
  }


  /**
   * Clears a StringBuffer
   *
   * @param buffer The StringBuffer to clear.
   */
  private void clear(StringBuffer buffer) {
    buffer.delete(0, buffer.length());
  }
}