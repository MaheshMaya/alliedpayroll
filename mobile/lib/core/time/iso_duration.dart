/// Parses the ISO-8601 durations the backend serialises java.time.Duration
/// as, such as `PT15M`, `PT168H` or `PT1H30M5.5S`. Date components (years,
/// months, days) are not sent.
Duration parseIsoDuration(String text) {
  final match = _shape.firstMatch(text);
  if (match == null) throw FormatException('not an ISO-8601 duration', text);
  final negative = match.group(1) == '-';
  final hours = int.parse(match.group(2) ?? '0');
  final minutes = int.parse(match.group(3) ?? '0');
  final seconds = double.parse(match.group(4) ?? '0');
  final total = Duration(
    hours: hours,
    minutes: minutes,
    microseconds: (seconds * Duration.microsecondsPerSecond).round(),
  );
  return negative ? -total : total;
}

final RegExp _shape = RegExp(
  r'^(-)?PT(?:(\d+)H)?(?:(\d+)M)?(?:(\d+(?:\.\d+)?)S)?$',
);
