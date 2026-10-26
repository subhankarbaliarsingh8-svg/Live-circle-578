# DesiConnect - Supabase Setup & Architecture Guide

DesiConnect is a modern Indian social media app inspired by ShareChat, built with Android Jetpack Compose, Kotlin, and Supabase.

---

## 1. Quick Supabase Setup (Beginner Friendly)

### Step 1: Create a Supabase Project
1. Go to [supabase.com](https://supabase.com) and create an account or sign in.
2. Click **New Project**, select an organization, and choose a region close to your target audience (e.g., `ap-south-1` Mumbai).
3. Name your project (e.g., `DesiConnect-App`) and set a strong database password.

### Step 2: Run the SQL Migrations
1. In the Supabase Dashboard, open the **SQL Editor** from the left navigation panel.
2. Click **New query**.
3. Open `supabase_schema.sql` located at the root of this project.
4. Copy the entire contents and paste into the Supabase SQL editor.
5. Click **Run**. This creates:
   - All 12 tables: `profiles`, `posts`, `likes`, `comments`, `follows`, `bookmarks`, `notifications`, `conversations`, `conversation_members`, `messages`, `reports`, `blocks`
   - Row-Level Security (RLS) policies
   - Automatic triggers for new user profiles
   - Storage buckets: `avatars`, `covers`, and `post-media`

### Step 3: Get Your API Keys & URL
1. Go to **Project Settings** -> **API**.
2. Copy:
   - **Project URL** (e.g., `https://xyzcompany.supabase.co`)
   - **Project API Keys** -> `anon` / `public` key

---

## 2. Configuring the App

### Option A: Using the In-App Setup UI (Instant)
1. Open the DesiConnect app.
2. In the top app bar, tap the **Supabase Status Cloud Icon** or go to **Profile -> Settings -> Supabase Backend**.
3. Paste your **Supabase URL** and **Anon Key**.
4. Tap **Save & Connect**. The app validates connection immediately!

### Option B: Using `.env` / BuildConfig
1. In the root directory, create or edit `.env`:
   ```bash
   SUPABASE_URL=https://your-project-id.supabase.co
   SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```
2. Build or reload the app. DesiConnect automatically reads these values via the Secrets Gradle Plugin.

---

## 3. Features Implemented in DesiConnect

| Feature | Description |
|---|---|
| 🏠 **Home Feed** | For You, Following, and Trending tabs with rich cards, media previews, like/bookmark animations, and share. |
| 🔍 **Discover & Trends** | Search creators & hashtags, trending hashtags (#DesiVibes, #BollywoodMasti), and categorized feeds. |
| ➕ **Post Composer** | Create rich posts with captions, hashtag chips, image/video previews, language tags, and instant publishing. |
| 🎬 **Desi Clips (Shorts)** | Full vertical video feed (Moj / ShareChat style) with interactive likes, sound tags, and comments. |
| 💬 **Private Messaging** | 1-on-1 chats with real-time message bubble UI, message timestamps, and unread counters. |
| 🔔 **Notifications** | Alerts for Likes, Comments, Mentions, and new Followers with mark-as-read. |
| 👤 **User Profiles** | Follow/Unfollow, followers count, bio, language preferences (Hindi & English), and saved bookmarks. |
| 🌐 **Bilingual Support** | Instant toggle between Hindi (हिंदी) and English throughout UI. |
| 🛡️ **Reliability Fallback** | Runs seamlessly in zero-crash fallback mode when offline or without keys, plus instant Supabase sync when keys are provided! |

---

## 4. Troubleshooting Common Errors

- **Error: "SUPABASE_URL is missing or invalid"**
  Ensure the URL starts with `https://` and has no trailing spaces.
- **Error: "401 Unauthorized"**
  Verify that you copied the `anon` public key, not the `service_role` secret key.
- **Error: "RLS violation / permission denied"**
  Ensure that you ran the SQL script in `supabase_schema.sql` which enables RLS and adds required permissive policies for authenticated and public reads.
