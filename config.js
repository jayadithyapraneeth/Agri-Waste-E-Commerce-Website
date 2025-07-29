/*
 * config.js
 * Put this in /js so it’s easy to import everywhere.
 */
const ENV = {
    /**
     * When you are developing locally, run:
     *   npm run dev   (or just open index.html)
     * and set BASE_URL to your ngrok / localhost tunnel.
     *
     * When the back-end is on a public server, change to its URL.
     */
    BASE_URL: 'http://localhost:8080',  // ← default for local testing
};
export default ENV;
