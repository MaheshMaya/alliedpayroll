//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:copy_with_extension/copy_with_extension.dart';
import 'package:json_annotation/json_annotation.dart';

part 'relabel_request.g.dart';

@CopyWith()
@JsonSerializable(
  checked: true,
  createToJson: true,
  disallowUnrecognizedKeys: false,
  explicitToJson: true,
)
class RelabelRequest {
  /// Returns a new [RelabelRequest] instance.
  RelabelRequest({required this.labels});

  @JsonKey(name: r'labels', required: true, includeIfNull: false)
  final Map<String, String> labels;

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is RelabelRequest && other.labels == labels;

  @override
  int get hashCode => labels.hashCode;

  factory RelabelRequest.fromJson(Map<String, dynamic> json) =>
      _$RelabelRequestFromJson(json);

  Map<String, dynamic> toJson() => _$RelabelRequestToJson(this);

  @override
  String toString() {
    return toJson().toString();
  }
}
