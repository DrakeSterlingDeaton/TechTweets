package website;

import java.util.HashMap;
import java.util.concurrent.Executors; // Inteface used to decouple "task submission from the mechanics of how each task will be run" (from: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executor.html)
import java.util.concurrent.ScheduledExecutorService; // Interface that can "schedule commands to run after a given delay" (from: https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ScheduledExecutorService.html)

import org.springframework.boot.context.event.ApplicationReadyEvent;    // "Event published as late as conceivably possible to indicate that the application is ready to service requests." (from: https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/context/event/ApplicationReadyEvent.html)
import org.springframework.context.event.EventListener;     // Used to listen for the Spring Boot 'ApplicationReadyEvent'

import java.io.*;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;             // Used to traverse HTML within Java
import org.jsoup.nodes.Document;    // Used to traverse HTML within Java
import org.jsoup.nodes.Element;     // Used to traverse HTML within Java

import org.springframework.stereotype.Component;
import twitter4j.JSONException;     // Used to Handle logic errors realted to the Twitter4j library
import twitter4j.*;                 // Used to fetch & process Tweet data
import twitter4j.conf.ConfigurationBuilder;    // Used to configure Twitter account authorization so requests for tweets can be made to the Twitter API
import static twitter4j.Query.ResultType.recent;    // query parameter used when fetching tweets from the Twitter API

import com.fasterxml.jackson.databind.ObjectMapper; // Used to create/append/traverse Json
import com.fasterxml.jackson.databind.SerializationFeature; // Used to to 'prettify' Json printing
import com.fasterxml.jackson.databind.node.ArrayNode; // Used to create/append/traverse Json
import com.fasterxml.jackson.databind.node.ObjectNode; // Used to create/append/traverse Json

import java.io.IOException;         // Used to handle logic errors
import java.nio.file.Files;         // Used to read, write, and delete files within this project
import java.nio.file.StandardOpenOption;    // ^^ ^^ ^^

@Component
public class RuntimeThread {

    private Twitter twitter;
    private LocalDate today;
    private JSONObject twtrData;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private File staticIndexFile = new File("src/main/resources/static/html/indexCopy.html");
    private File triggerFile = new File("src/main/resources/.restartTriggerFile");
    private HashMap<String, Boolean> tweetDict = new HashMap<>();
    private File templateIndexFile = new File(getClass().getClassLoader().getResource("templates/index.html").getFile()); // Line of code adapted from "https://stackoverflow.com/questions/29745164/java-io-filenotfoundexception-when-using-jsoup" on Nov 26th 2019
    private File tweetHTML = new File(getClass().getClassLoader().getResource("static/html/tweetHTML.html").getFile());
    private File jsonLdSocialMediaPost = new File("src/main/resources/static/js/jsonLdSocialMediaPost.js");
    private File indexJsonLdFile = new File("src/main/resources/static/js/indexJsonLd.js");
    private ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @EventListener(ApplicationReadyEvent.class)
    public void twitterThread() {
        System.out.println("Beginning thread loop!");
        today = LocalDate.now();
        initTwtrAuth(); // Handles twitter authorzation
        executor.schedule(threadLoop(), 5, TimeUnit.SECONDS); // Initiates thread loop
    }

    private Runnable threadLoop() {
        // Loop actions
        System.out.println("'threadLoop' function called!");
        JSONObject data = getTwitterData();  // fetching twitter data
        if (data != null) { updateIndexHTML(data); };     // updating HTML scripts
        try {
            triggerSpringResourceRefresh();  // forcing Spring Boot to refresh resources. Resources set to refresh when a 'trigger file' is updated. Trigger file is set in the script '.sprint-boot-devtools.properties'. See devtools documentation for more information.)
            assert LocalDate.now().equals(today);   // ensure that loop won't continue overnight if left on.
            return () -> executor.schedule(threadLoop(), 5, TimeUnit.SECONDS); // initiates another thread after 10 seconds
        } catch (Exception e) {
            return () -> executor.shutdown();
        }
    }

//////////////////////////// TWITTER DATA REQUEST/EXTRACTION METHODS ////////////////////////////

    private void initTwtrAuth() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("cWHDt41fqYOOpjsEABq2MYRDo")
                .setOAuthConsumerSecret("lbSv0gH0lZdPVkjI0iH8hk75ZMRWWmyK11ALbCRoi0z3LePqB5")
                .setOAuthAccessToken("800761746948358144-cQPIRAeG3Zaw4An3nlWqY4xy9xUrLmZ")
                .setOAuthAccessTokenSecret("zlFzsNpfiajdAkuXSXAoAQwwA8MK17p5S0Q1EHwn0TI5Q");
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }

    private Status sendGetReq() {
        // Function: Sends GET reg to twitter to search for tweets related to software engineering.
        // Returns to first tweet found that isn't a retweet, else returns null.
        System.out.println("sending req...");
        try {
            Query query = new Query("software");              // Query part of Twitter URL API request
            query.setResultType(recent);                        // More API URL data: query returns only reccent tweets
            query.setCount(10);                                 // More API URL data: query returns only 10 tweets
            query.setLocale("en");                              // More API URL data: query returns only tweets from english speaking countires
            query.setLang("en");                                // More API URL data: query returns only tweets written in english
            QueryResult result = twitter.search(query);         // perform GET request
            for (Status status : result.getTweets()) {          // iterate over array of Status objects (status obj's are obj's containing data on a tweet)
                if ( checkTweetHasntBeenAddedPreviously(status.getText()) && !(status.isRetweet()) ) {
                    System.out.println("Appropriate Tweet found...");
                    return status;                              //      context without fetching additional tweets, so they're not being used)
                }
            }
        } catch (Exception e) { e.printStackTrace(); };
        return null;
    }

    private boolean checkTweetHasntBeenAddedPreviously(String text) {
        if (tweetDict.containsKey(text)) {  // if tweet is a kew in the tweetDict...
            return false;    // false, this tweet is already on our records
        } else {
            tweetDict.put(text, true);  // Tweet isn't in tweetDict, so adding it to the dict
            return true;    // True, this tweet hasn't occured before
        }
    }

    private JSONObject extractTwtrData(Status resStatus) throws JSONException {
        // Function: Takes in a Status object (contains data on a tweet), extracts all the needed data
        // returns a JSONObject containing all of the needed data.
        User usr = resStatus.getUser();         // Inititalizng User obj (contains data on an individual user) based on user data returned from function
        twtrData = new JSONObject();            // Reinitializing twterData obj so that'll be cleaned of previous data if it's been accessed previously.
        twtrData.put("TweetTxt", resStatus.getText());
        twtrData.put("CreatedAt", resStatus.getCreatedAt());
        twtrData.put("RetweetCnt", resStatus.getRetweetCount());
        twtrData.put("FavCnt", resStatus.getFavoriteCount());
        twtrData.put("UsrProfileImageURL", usr.getProfileImageURLHttps());
        twtrData.put("UsrName", usr.getScreenName());
        twtrData.put("UsrURL", usr.getURL());
        return twtrData;
    }

    private JSONObject getTwitterData() {
        try {
            Status resStatus = sendGetReq();
            twtrData = (resStatus != null) ? extractTwtrData(resStatus) : null;
            System.out.println("Twitter Req successful");
            return twtrData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//////////////////////////// HTML CREATION METHODS ////////////////////////////

    private void updateIndexHTML(JSONObject twitterData) {
        try {
            Element newTweetBox = formatNewTweetBox(twitterData);
            addHTMLtoTweetFeedFiles(newTweetBox);
            ObjectNode newTweetJsonLd = formatNewJsonLdObj();
            addJsonLdToIndexJSFile(newTweetJsonLd);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Element formatNewTweetBox(JSONObject data) throws IOException {
        try {
            // Parsing HTML file & isolating HTML of a box for a new tweet
            Document doc = Jsoup.parse(tweetHTML,  "utf-8"); // Using path to file to create copy of HTML file that's traversable in Java
            Element tweetBox = doc.getElementsByClass("SEBox").get(0).clone();  // Creating copy of HTML

            // Selecting HTML elements to add new tweet data to
            Element username = tweetBox.getElementsByClass("username").get(0);  // Selecting element to add username to
            Element usrPic = tweetBox.getElementsByClass("SEPicBoxInner").get(0); // Selecting element to add user display picture to
            Element tweetText = tweetBox.getElementsByClass("TweetText").get(0); // selecting element to add tweettext to

            // Adding new tweet data to HTML elements
            username.empty().appendText(twtrData.getString("UsrName"));  // Using Twitter data to update innerHTML
            tweetText.empty().appendText(twtrData.getString("TweetTxt")); // Using Twitter data to update innerHTML
            usrPic.attr("src", twtrData.getString("UsrProfileImageURL")); // Using Twitter data to update IMG tag src attribute

            // Adding optional tweet data (some tweets from the Twitter API don't have this data associated with them, such as a user HREF♦)
            try {
                Element usernameLink = tweetBox.getElementsByClass("usernameLink").get(0); // Select element to add link to user's Twitter page to
                usernameLink.attr("href", twtrData.getString("UsrURL")); // Using Twitter data to update A tag href attribute
            } catch (JSONException e) { System.out.println("No link associated with this username"); }

            return tweetBox;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addHTMLtoTweetFeedFiles(Element newTweetBox) throws IOException {
        Document indexHTML = Jsoup.parse(staticIndexFile,  "utf-8"); // Using path to file to create copy of HTML file that's traversable in Java
        Element feed = indexHTML.getElementById("SEFeed");  // Finding the feed div
        newTweetBox.appendTo(feed);     // appending 'newTweetBox' child div to end of feed parent div

        // Replacing static tweet feed HTML file with new version
        Files.delete(staticIndexFile.toPath());
        Files.write(staticIndexFile.toPath(), indexHTML.toString().getBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);   //StandardCharsets.UTF_8
        System.out.println("Added new tweet HTML to static HTML");

        // Replacing TEMPLATE HTML file with new version
        Files.delete(templateIndexFile.toPath());
        Files.write(templateIndexFile.toPath(), indexHTML.toString().getBytes(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);   //StandardCharsets.UTF_8
        System.out.println("Added new tweet HTML to template HTML");
    }

    private ObjectNode formatNewJsonLdObj() throws IOException, JSONException {

        // Identifying JSON LD objects
        ObjectNode socialMediaPost = (ObjectNode) objectMapper.readTree(jsonLdSocialMediaPost);
        ObjectNode author = (ObjectNode) socialMediaPost.get("author");
        ObjectNode comment = (ObjectNode) socialMediaPost.get("comment");

        // Adding JSON LD fields
        author.put("name", twtrData.getString("UsrName"));
        author.put("image", twtrData.getString("UsrProfileImageURL"));
        comment.put("text", twtrData.getString("TweetTxt"));

        // Add Option JSON LD fields
        try { author.put("sameAs", twtrData.getString("UsrURL"));
        } catch (Exception e) { System.out.println("No link available. Skipping link"); }

        // Returning JSON LD for new social media post
        System.out.println("Creating JSON LD for new tweet...");
        return socialMediaPost;
    }

    private void addJsonLdToIndexJSFile(ObjectNode socialMediaPost) throws IOException {
        ObjectNode indexJsonLd = (ObjectNode) objectMapper.readTree(indexJsonLdFile);

        // Adding new JSONLD obj to JSONLD Arr
        ArrayNode indexJsonLdArr = (ArrayNode) indexJsonLd.get("itemListElement");  // Identify Node Arr
        socialMediaPost.put("position", indexJsonLdArr.size()+1); // Adding the position within the Arr to the new node to be appended to the end of the arr
        indexJsonLdArr.add(socialMediaPost);    // Appending new node to end of Arr
        indexJsonLd.put("numberOfItems", indexJsonLdArr.size());  // Updating total length of Arr

        // Rewritting JSONLD file
        Files.delete(indexJsonLdFile.toPath());
        Files.write(indexJsonLdFile.toPath(), objectMapper.writeValueAsString(indexJsonLd).getBytes(),
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE);   //StandardCharsets.UTF_8
        System.out.println("Added new Json LD data to indexJsonLd file");
    }

    private void triggerSpringResourceRefresh() throws IOException {
        Files.write(triggerFile.toPath(), "".getBytes(), StandardOpenOption.APPEND);   //StandardCharsets.UTF_8
        System.out.println("Static resources refresh triggered");
        System.out.println("Awaiting next threadLoop...");
        System.out.println("");
    }

}