export default function StatusBadge({ status }) {
  if (status === "ACTIVE") {
    return (
      <span className="badge badge-live">
        <span className="dot" />
        Live
      </span>
    );
  }
  if (status === "SCHEDULED") {
    return (
      <span className="badge badge-scheduled">
        <span className="dot" />
        Scheduled
      </span>
    );
  }
  return (
    <span className="badge badge-ended">
      <span className="dot" />
      Ended
    </span>
  );
}