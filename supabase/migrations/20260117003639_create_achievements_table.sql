/*
  # Create Achievements Table

  1. New Tables
    - `achievements`
      - `id` (bigint, primary key, auto-increment)
      - `player_uuid` (uuid, not null) - Player's UUID
      - `achievement_id` (text, not null) - Achievement identifier
      - `unlocked_at` (timestamptz, default now()) - When achievement was unlocked
      - `created_at` (timestamptz, default now())

  2. Indexes
    - Index on `player_uuid` for faster queries
    - Unique constraint on `player_uuid` and `achievement_id`

  3. Security
    - Enable RLS on `achievements` table
    - Add policies for authenticated access

  ## Notes
  - Stores player achievement unlocks
  - Unique constraint prevents duplicate unlocks
*/

CREATE TABLE IF NOT EXISTS achievements (
  id bigserial PRIMARY KEY,
  player_uuid uuid NOT NULL,
  achievement_id text NOT NULL,
  unlocked_at timestamptz DEFAULT now(),
  created_at timestamptz DEFAULT now(),
  UNIQUE (player_uuid, achievement_id)
);

CREATE INDEX IF NOT EXISTS idx_achievements_player_uuid ON achievements(player_uuid);

ALTER TABLE achievements ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Allow server to read all achievements"
  ON achievements
  FOR SELECT
  USING (true);

CREATE POLICY "Allow server to insert achievements"
  ON achievements
  FOR INSERT
  WITH CHECK (true);