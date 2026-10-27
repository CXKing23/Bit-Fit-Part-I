# Android Project 5 — BitFit

Submitted by: Christian King

**BitFit** is a daily hydration tracker. Record the amount of water you drink each day in fluid ounces, review past entries, and see a month-long intake chart and average. A sample month is added on first launch so the calendar and trends are populated for a demo.

Time spent: 5

## Required Features

- [x] At least one health metric is tracked based on user input
  - Chosen metric: daily water intake, in fluid ounces
- [x] A create-entry UI prompts users to make their daily entry
- [x] New entries are saved in a SQLite database and displayed in the RecyclerView
- [x] Entries persist after the app is restarted

## Optional Features

- [x] Calendar view, monthly average, and daily trend chart
- [x] Custom colors and styling
- [ ] Orientation responsiveness
- [ ] Daily photo feature

## Additional Features

- One entry is kept per calendar day; saving again updates that day's total.
- Sample entries are clearly marked “demo” and only seeded when the database has no entries.
- Amounts are validated before saving.

## Video Walkthrough

Add a GIF or video walkthrough here after recording the finished app.

## Notes

The project began from the Android Studio starter screen. The main implementation work was replacing the sample navigation flow with a persistent daily log and summary.

## License

Copyright 2026 Christian King

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
