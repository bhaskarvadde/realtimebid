import { useEffect, useState } from "react";
import { formatCompact, splitDuration } from "../utils/format";

function useNow() {
  const [now, setNow] = useState(() => Date.now());
  useEffect(() => {
    const timer = setInterval(() => setNow(Date.now()), 1000);
    return () => clearInterval(timer);
  }, []);
  return now;
}

const pad = (n) => String(n).padStart(2, "0");

export default function Countdown({ status, startTime, endTime, variant = "compact" }) {
  const now = useNow();

  if (status === "ENDED") {
    return variant === "big" ? (
      <div className="countdown-ended">Auction ended</div>
    ) : (
      <span className="countdown">Ended</span>
    );
  }

  const target = new Date(status === "SCHEDULED" ? startTime : endTime);
  const remaining = target.getTime() - now;
  const prefix = status === "SCHEDULED" ? "Starts in" : "Ends in";

  if (remaining <= 0) {
    return variant === "big" ? (
      <div className="countdown-ended">Finalizing…</div>
    ) : (
      <span className="countdown">Finalizing…</span>
    );
  }

  if (variant === "compact") {
    return <span className="countdown">{formatCompact(remaining)}</span>;
  }

  const { days, hours, minutes, seconds } = splitDuration(remaining);
  return (
    <div>
      <div className="metric-label" style={{ marginBottom: 10 }}>{prefix}</div>
      <div className="cd-row">
        {[["Days", days], ["Hours", hours], ["Min", minutes], ["Sec", seconds]].map(([label, value]) => (
          <div className="cd-seg" key={label}>
            <div className="cd-num">{pad(value)}</div>
            <div className="cd-lab">{label}</div>
          </div>
        ))}
      </div>
    </div>
  );
}