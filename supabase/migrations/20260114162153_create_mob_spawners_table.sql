/*
  # Create mob spawners table

  1. New Tables
    - `mob_spawners`
      - `id` (uuid, primary key) - Unique spawner identifier
      - `world` (text) - World name where spawner is located
      - `x` (integer) - X coordinate
      - `y` (integer) - Y coordinate
      - `z` (integer) - Z coordinate
      - `mob_id` (text) - Custom mob ID to spawn
      - `spawn_interval` (integer) - Seconds between spawns
      - `max_nearby_mobs` (integer) - Maximum mobs that can exist nearby
      - `spawn_radius` (integer) - Radius to check for nearby mobs
      - `active` (boolean) - Whether spawner is active
      - `created_at` (timestamptz) - When spawner was placed
      - `last_spawn` (bigint) - Last spawn timestamp (milliseconds)

  2. Security
    - Enable RLS on `mob_spawners` table
    - Add policy for authenticated users to manage spawners
*/

CREATE TABLE IF NOT EXISTS mob_spawners (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  world text NOT NULL,
  x integer NOT NULL,
  y integer NOT NULL,
  z integer NOT NULL,
  mob_id text NOT NULL,
  spawn_interval integer DEFAULT 30,
  max_nearby_mobs integer DEFAULT 3,
  spawn_radius integer DEFAULT 16,
  active boolean DEFAULT true,
  created_at timestamptz DEFAULT now(),
  last_spawn bigint DEFAULT 0
);

ALTER TABLE mob_spawners ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Service role can manage all spawners"
  ON mob_spawners
  FOR ALL
  TO service_role
  USING (true)
  WITH CHECK (true);

CREATE INDEX IF NOT EXISTS idx_mob_spawners_location ON mob_spawners(world, x, y, z);
CREATE INDEX IF NOT EXISTS idx_mob_spawners_active ON mob_spawners(active);
