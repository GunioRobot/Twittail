/**
 *
 */
package info.kuborn.twittail.scala

/**
 * @author kuborn
 *
 */
object Twittail {
  import scala.collection.JavaConverters._

  import _root_.twitter4j._
  import _root_.twitter4j.auth._
  import _root_.scala.collection.mutable.Buffer;

  def main(args: Array[String]): Unit = {
    println("====== Twittail(tail command for Twitter) =======")

    val twitter = new TwitterFactory().getInstance()
    var lastTweetId = 0L

    val f = parseOptions(args, twitter);
    while (true) {
      try {
        f().foreach { status =>
          status match {
            case status: twitter4j.Status => {
              if (lastTweetId < status.getId) {
                // new
                lastTweetId = status.getId
                display(status.getCreatedAt(), (status.getUser().getName() + "(" + status.getUser().getScreenName() + ")"), status.getText())
              } else {
                //println(">>>>>>>> do nothing")
              }
            }
            case tweet: twitter4j.Tweet => {
              if (lastTweetId < tweet.getId) {
                // new
                lastTweetId = tweet.getId
                display(tweet.getCreatedAt(), (tweet.getFromUser() + ""), tweet.getText())
              } else {
                //println(">>>>>>>> do nothing")
              }
            }
            case _ => {
              println("cannot display")
            }
          }
        }
      } catch {
        case e: TwitterException => println("an error occured: " + e);
      }

      Thread.sleep(12000)
    }

    println("main  end ")
  }

  /**
   * parse command line args
   */
  def parseOptions(args: Seq[String], twitter: twitter4j.Twitter): (() => Buffer[_ <: Any]) = {
    var list = List.concat(args)
    list match {
      case "-f" :: "public" :: rest => {
        () =>
          twitter.getPublicTimeline().asScala.reverse
      }
      case "-f" :: "home" :: rest => {
        () =>
          twitter.getHomeTimeline().asScala.reverse
      }
      case "-f" :: "user" :: user :: rest => {
        () =>
          twitter.getUserTimeline(user).asScala.reverse
      }
      case "-f" :: "search" :: query :: rest => {
        () =>
          twitter.search(new twitter4j.Query(query)).getTweets().asScala.reverse
      }
      case _ => {
        println("Warning: mode is invalid. So now public timeline mode")
        () =>
          twitter.getPublicTimeline().asScala.reverse
      }
    }
  }

  /**
   *  display status in console
   */
  def display(createdAt: java.util.Date, name: String, text: String): Unit = {
    printf("[%tF %<tT] %s: ", createdAt, name)
    println(text)
  }

  /**
   * display usage info
   */
  def usage(): Unit = {

  }

}

